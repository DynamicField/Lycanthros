package com.github.df.loupsgarous.interaction;

import com.github.df.loupsgarous.*;
import com.github.df.loupsgarous.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.reflect.TypeToken;

import java.util.*;
import java.util.stream.Collectors;

public interface InteractableRegistry {
    ImmutableSet<Interactable> get(String key);

    SafeSingleBuilder<Interactable> single(String key);

    Registration register(String key, Interactable value);

    void unregister(Interactable value);

    ImmutableSetMultimap<String, Interactable> getAll();

    interface MultipleItemsHandler<T extends Interactable> {
        SafeResult<T> handle(String key, Set<T> interactables);
    }

    class SafeSingleBuilder<T extends Interactable> {
        private final InteractableRegistry registry;
        private final String key;

        private final List<CheckPredicate<? super T>> checkPredicates = new ArrayList<>(1);
        private String failureMessage = "Impossible de faire ça maintenant";
        private MultipleItemsHandler<T> multipleItemsHandler = this::defaultMultipleItemsHandler;
        private TypeToken<?> targetType;

        public SafeSingleBuilder(InteractableRegistry registry,
                String key, TypeToken<T> targetType) {
            this.registry = registry;
            this.key = key;
            this.targetType = targetType;
        }

        public ActualInteractableRegistry.SafeSingleBuilder<T> check(CheckPredicate<? super T> checkPredicate) {
            checkPredicates.add(Objects.requireNonNull(checkPredicate));
            return this;
        }

        public ActualInteractableRegistry.SafeSingleBuilder<T> failureMessage(String failureMessage) {
            this.failureMessage = Objects.requireNonNull(failureMessage);
            return this;
        }

        public ActualInteractableRegistry.SafeSingleBuilder<T> multipleItemsHandler(MultipleItemsHandler<T> handler) {
            this.multipleItemsHandler = Objects.requireNonNull(handler);
            return this;
        }

        public <N extends Interactable> ActualInteractableRegistry.SafeSingleBuilder<N> type(Class<N> targetType) {
            return type(TypeToken.of(targetType));
        }

        @SuppressWarnings("unchecked")
        public <N extends Interactable> ActualInteractableRegistry.SafeSingleBuilder<N> type(TypeToken<N> targetType) {
            this.targetType = targetType;
            return (ActualInteractableRegistry.SafeSingleBuilder<N>) this;
        }

        public SafeResult<T> get() {
            Set<T> interactables = getInteractables();
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

        @SuppressWarnings("unchecked")
        private Set<T> getInteractables() {
            ImmutableSet<Interactable> unfiltered = registry.get(key);
            HashSet<T> filtered = new HashSet<>();

            for (Interactable interactable : unfiltered) {
                if (targetType.isSupertypeOf(interactable.getClass())) {
                    filtered.add((T) interactable);
                }
            }

            return filtered;
        }

        private FilterResults filterInteractables(Set<T> interactables) {
            if (checkPredicates.isEmpty()) {
                return new FilterResults(interactables, Collections.emptyList());
            }

            Set<T> validInteractables = new HashSet<>();
            List<Check> failedChecks = new ArrayList<>();

            for (T interactable : interactables) {
                Check check = CheckStreams.shortCircuitingAnd(
                        checkPredicates.stream().map(x -> x.check(interactable)));

                if (check.isSuccess()) {
                    validInteractables.add(interactable);
                } else {
                    failedChecks.add(check);
                }
            }

            return new FilterResults(validInteractables, failedChecks);
        }

        private SafeResult<T> defaultMultipleItemsHandler(String key, Set<T> interactables) {
            throw new MultipleInteractablesException(
                    "Multiple interactables have been found for key " + key + ": [" +
                    interactables.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]"
            );
        }

        public Optional<T> getOptional() {
            return get().getValueOptional();
        }

        public Interactable getOrNull() {
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
}
