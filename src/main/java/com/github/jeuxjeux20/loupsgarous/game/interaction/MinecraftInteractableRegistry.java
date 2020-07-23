package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.AbstractOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.CheckPredicate;
import com.github.jeuxjeux20.loupsgarous.util.CheckStreams;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import me.lucko.helper.terminable.composite.CompositeClosingException;

import java.util.*;
import java.util.stream.Collectors;

@OrchestratorScoped
class MinecraftInteractableRegistry
        extends AbstractOrchestratorComponent
        implements InteractableRegistry {
    private final SetMultimap<InteractableKey<?>, Interactable> map =
            MultimapBuilder.hashKeys().hashSetValues().build();

    @Inject
    MinecraftInteractableRegistry(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        bind(this::terminate);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Interactable> ImmutableSet<T> get(InteractableKey<T> key) {
        ensureValidKey(key);

        // Safe because we check the key type every time we register something.
        Set<? extends T> interactables = (Set<? extends T>) map.get(key);

        return ImmutableSet.copyOf(interactables);
    }

    @Override
    public <T extends Interactable> SafeSingleBuilder<T> single(InteractableKey<T> key) {
        ensureValidKey(key);

        return new SafeSingleBuilderImpl<>(key);
    }

    @Override
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

    @Override
    public <T extends Interactable> boolean remove(InteractableKey<? super T> key, T value) {
        ensureValidKey(key);

        return map.remove(key, value);
    }

    @Override
    public Optional<InteractableKey<?>> findKey(String name) {
        return map.keySet().stream().filter(x -> x.getName().equals(name)).findAny();
    }

    @Override
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
        Preconditions.checkArgument(interactable.gameOrchestrator() == orchestrator,
                "The interactable value's orchestrator (" + interactable.gameOrchestrator() + ") " +
                "is not the same as this registry's orchestrator: " + orchestrator);
    }

    private final class SafeSingleBuilderImpl<T extends Interactable> implements SafeSingleBuilder<T> {
        private final InteractableKey<T> key;

        private final List<CheckPredicate<? super T>> checkPredicates = new ArrayList<>(1);
        private String failureMessage = "Impossible de faire ça maintenant";
        private MultipleItemsHandler<T> multipleItemsHandler = this::defaultMultipleItemsHandler;

        SafeSingleBuilderImpl(InteractableKey<T> key) {
            this.key = key;
        }

        @Override
        public SafeSingleBuilder<T> check(CheckPredicate<? super T> checkPredicate) {
            checkPredicates.add(Objects.requireNonNull(checkPredicate));
            return this;
        }

        @Override
        public SafeSingleBuilder<T> failureMessage(String failureMessage) {
            this.failureMessage = Objects.requireNonNull(failureMessage);
            return this;
        }

        @Override
        public SafeSingleBuilder<T> multipleItemsHandler(MultipleItemsHandler<T> handler) {
            this.multipleItemsHandler = Objects.requireNonNull(handler);
            return this;
        }

        @Override
        public SafeResult<T> get() {
            ImmutableSet<T> interactables = MinecraftInteractableRegistry.this.get(key);
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

        private final class FilterResults {
            final Set<T> validInteractables;
            final List<Check> failedChecks;

            private FilterResults(Set<T> validInteractables, List<Check> failedChecks) {
                this.validInteractables = validInteractables;
                this.failedChecks = failedChecks;
            }
        }
    }
}
