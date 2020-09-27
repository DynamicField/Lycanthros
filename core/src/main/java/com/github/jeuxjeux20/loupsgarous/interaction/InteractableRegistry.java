package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.Check;
import com.github.jeuxjeux20.loupsgarous.CheckPredicate;
import com.github.jeuxjeux20.loupsgarous.CheckStreams;
import com.github.jeuxjeux20.loupsgarous.SafeResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import me.lucko.helper.terminable.composite.CompositeClosingException;

import java.util.*;
import java.util.stream.Collectors;

public class InteractableRegistry extends OrchestratorComponent {
    private final SetMultimap<InteractableKey<?>, Interactable> map =
            MultimapBuilder.hashKeys().hashSetValues().build();

    @Inject
    InteractableRegistry(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        bind(this::terminate);
    }

    @SuppressWarnings("unchecked")
    public <T extends Interactable> ImmutableSet<T> get(InteractableKey<T> key) {
        ensureValidKey(key);

        // Safe because we check the key type every time we register something.
        Set<? extends T> interactables = (Set<? extends T>) map.get(key);

        return ImmutableSet.copyOf(interactables);
    }

    public <T extends Interactable> SafeSingleBuilder<T> single(InteractableKey<T> key) {
        ensureValidKey(key);

        return new SafeSingleBuilder<>(key);
    }

    public <T extends Interactable> boolean register(InteractableKey<? super T> key, T value) {
        ensureValidKey(key);
        ensureNotTerminated(value);
        ensureSameOrchestrator(value);

        boolean hasBeenAdded = map.put(key, value);
        if (hasBeenAdded) {
            value.addTerminationListener(i -> remove(key, value));
        }
        return hasBeenAdded;
    }

    public <T extends Interactable> boolean remove(InteractableKey<? super T> key, T value) {
        ensureValidKey(key);

        return map.remove(key, value);
    }

    public Optional<InteractableKey<?>> findKey(String name) {
        return map.keySet().stream().filter(x -> x.getName().equals(name)).findAny();
    }

    public ImmutableSetMultimap<InteractableKey<?>, Interactable> getAll() {
        return ImmutableSetMultimap.copyOf(map);
    }

    private void terminate() throws CompositeClosingException {
        List<Exception> closingExceptions = new ArrayList<>();

        for (Interactable value : map.values()) {
            if (!value.isClosed()) {
                try {
                    value.close();
                } catch (Exception e) {
                    closingExceptions.add(e);
                }
            }
        }

        map.clear();

        if (!closingExceptions.isEmpty()) {
            throw new CompositeClosingException(closingExceptions);
        }
    }

    private void ensureValidKey(InteractableKey<?> key) {
        Optional<InteractableKey<?>> maybeActualKey = findKey(key.getName());

        maybeActualKey.ifPresent(actualKey -> {
            if (!key.getType().equals(actualKey.getType())) {
                throw new IllegalArgumentException("Given key " + key + " does not have the same type as " +
                                                   "the actual key " + actualKey + ".");
            }
        });
    }

    private void ensureNotTerminated(Interactable interactable) {
        if (interactable.isClosed()) {
            throw new IllegalStateException("The given value has already been closed.");
        }
    }

    private void ensureSameOrchestrator(Interactable interactable) {
        Preconditions.checkArgument(interactable.getOrchestrator() == orchestrator,
                "The interactable value's orchestrator (" + interactable.getOrchestrator() + ") " +
                "is not the same as this registry's orchestrator: " + orchestrator);
    }

    public final class SafeSingleBuilder<T extends Interactable> {
        private final InteractableKey<T> key;

        private final List<CheckPredicate<? super T>> checkPredicates = new ArrayList<>(1);
        private String failureMessage = "Impossible de faire ça maintenant";
        private MultipleItemsHandler<T> multipleItemsHandler = this::defaultMultipleItemsHandler;

        private SafeSingleBuilder(InteractableKey<T> key) {
            this.key = key;
        }

        public SafeSingleBuilder<T> check(CheckPredicate<? super T> checkPredicate) {
            checkPredicates.add(Objects.requireNonNull(checkPredicate));
            return this;
        }

        public SafeSingleBuilder<T> failureMessage(String failureMessage) {
            this.failureMessage = Objects.requireNonNull(failureMessage);
            return this;
        }

        public SafeSingleBuilder<T> multipleItemsHandler(MultipleItemsHandler<T> handler) {
            this.multipleItemsHandler = Objects.requireNonNull(handler);
            return this;
        }

        public SafeResult<T> get() {
            ImmutableSet<T> interactables = InteractableRegistry.this.get(key);
            if (interactables.size() == 0) {
                return SafeResult.error(failureMessage);
            }

            FilterResults results = filterInteractables(interactables);

            // Poor man's tuple unboxing
            Set<T> validInteractables = results.validInteractables;
            List<Check> failedChecks = results.failedChecks;

            if (validInteractables.size() == 1) {
                return SafeResult.success(validInteractables.iterator().next());
            } else if (validInteractables.size() == 0) {
                if (failedChecks.size() == 1) {
                    return SafeResult.error(failedChecks.get(0).getErrorMessage());
                } else {
                    // Putting multiple error messages at once is weird, just use the failure message.
                    return SafeResult.error(failureMessage);
                }
            } else {
                return multipleItemsHandler.handle(key, validInteractables);
            }
        }

        private FilterResults filterInteractables(ImmutableSet<T> interactables) {
            if (checkPredicates.isEmpty()) {
                return new FilterResults(interactables, Collections.emptyList());
            }

            Set<T> validInteractables = new HashSet<>();
            List<Check> failedChecks = new ArrayList<>();

            for (T interactable : interactables) {
                Check check = CheckStreams.shortCircuitingAnd(checkPredicates.stream().map(x -> x.check(interactable)));

                if (check.isSuccess()) {
                    validInteractables.add(interactable);
                } else {
                    failedChecks.add(check);
                }
            }

            return new FilterResults(validInteractables, failedChecks);
        }

        private SafeResult<T> defaultMultipleItemsHandler(InteractableKey<T> key, Set<T> interactables) {
            throw new MultipleInteractablesException(
                    "Multiple interactables have been found for key " + key + ": [" +
                    interactables.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]"
            );
        }

        public Optional<T> getOptional() {
            return get().getValueOptional();
        }

        public T getOrNull() {
            return getOptional().orElse(null);
        }

        private final class FilterResults {
            final Set<T> validInteractables;
            final List<Check> failedChecks;

            private FilterResults(Set<T> validInteractables, List<Check> failedChecks) {
                this.validInteractables = validInteractables;
                this.failedChecks = failedChecks;
            }
        }
    }

    public interface MultipleItemsHandler<T extends Interactable> {
        SafeResult<T> handle(InteractableKey<T> key, Set<T> interactables);
    }
}
