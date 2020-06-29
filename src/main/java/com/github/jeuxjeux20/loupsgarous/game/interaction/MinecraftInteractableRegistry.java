package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.CheckPredicate;
import com.github.jeuxjeux20.loupsgarous.util.CheckStreams;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.collect.*;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.terminable.composite.CompositeClosingException;

import java.util.*;
import java.util.stream.Collectors;

class MinecraftInteractableRegistry implements InteractableRegistry {
    private final SetMultimap<InteractableKey<?>, Interactable> map = MultimapBuilder.hashKeys().hashSetValues().build();

    @Inject
    MinecraftInteractableRegistry(@Assisted LGGameOrchestrator orchestrator) {
        bindWith(orchestrator);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Interactable> ImmutableSet<T> get(InteractableKey<T> key) {
        cleanTerminatedValues(key);

        // Safe because we check the key type every time we put something.
        Set<? extends T> interactables = (Set<? extends T>) map.get(key);

        return ImmutableSet.copyOf(interactables);
    }

    @Override
    public <T extends Interactable> SafeSingleBuilder<T> single(InteractableKey<T> key) {
        return new SafeSingleBuilderImpl<>(key);
    }

    @Override
    public <T extends Interactable> boolean put(InteractableKey<T> key, T value) {
        checkKeyType(key);

        return map.put(key, value);
    }

    @Override
    public <T extends Interactable> boolean remove(InteractableKey<T> key, T value) {
        // Close the interactable before we remove it.
        value.closeAndReportException();

        return map.remove(key, value);
    }

    @Override
    public <T extends Interactable> boolean has(InteractableKey<T> key, T value) {
        cleanTerminatedValues(key);

        return map.containsEntry(key, value);
    }

    @Override
    public Optional<InteractableKey<?>> findKey(String name) {
        return map.keySet().stream().filter(x -> x.getName().equals(name)).findAny();
    }

    @Override
    public ImmutableSetMultimap<InteractableKey<?>, Interactable> getAll() {
        ImmutableSet.copyOf(map.keySet()).forEach(this::cleanTerminatedValues);

        return ImmutableSetMultimap.copyOf(map);
    }

    @Override
    public void close() throws Exception {
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

    private void checkKeyType(InteractableKey<?> key) {
        Optional<InteractableKey<?>> maybeActualKey = findKey(key.getName());

        maybeActualKey.ifPresent(actualKey -> {
            if (!key.getType().equals(actualKey.getType())) {
                throw new IllegalArgumentException("Given key " + key + " does not have the same type as " +
                                                   "the actual key " + actualKey + ".");
            }
        });
    }

    private void cleanTerminatedValues(InteractableKey<?> key) {
        map.get(key).removeIf(Interactable::isClosed);
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
