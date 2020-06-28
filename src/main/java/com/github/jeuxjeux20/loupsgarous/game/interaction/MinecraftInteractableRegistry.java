package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.interaction.finders.InteractableFinder;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.CheckPredicate;
import com.github.jeuxjeux20.loupsgarous.util.CheckStreams;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.*;
import java.util.stream.Collectors;

class MinecraftInteractableRegistry implements InteractableRegistry {
    private final LGGameOrchestrator orchestrator;
    private final InteractableFinder interactableFinder;

    @Inject
    MinecraftInteractableRegistry(@Assisted LGGameOrchestrator orchestrator,
                                  InteractableFinder interactableFinder) {
        this.orchestrator = orchestrator;
        this.interactableFinder = interactableFinder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Interactable> ImmutableSet<T> get(InteractableKey<T> key) {
        // Safe because the map is created using getAll().
        return (ImmutableSet<T>) getAll().get(key);
    }

    @Override
    public <T extends Interactable> SafeSingleBuilder<T> single(InteractableKey<T> key) {
        return new SafeSingleBuilderImpl<>(key);
    }

    @Override
    public <T extends Interactable> boolean isPresent(InteractableEntry<T> entry) {
        return getAll().containsEntry(entry.getKey(), entry.getValue());
    }

    @Override
    public ImmutableSetMultimap<InteractableKey<?>, Interactable> getAll() {
        ImmutableSetMultimap.Builder<InteractableKey<?>, Interactable> builder = ImmutableSetMultimap.builder();

        Set<InteractableEntry<?>> entries = interactableFinder.find(orchestrator);
        for (InteractableEntry<?> entry : entries) {
            builder.put(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    private <T extends Interactable> MultipleInteractablesException createMultipleInteractablesException(
            InteractableKey<T> key, Set<T> interactables) {
        return new MultipleInteractablesException(
                "Multiple interactables have been found for key " + key + ": [" +
                interactables.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]"
        );
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
            }
            else if (validInteractables.size() == 0) {
                if (failedChecks.size() == 1) {
                    return SafeResult.error(failedChecks.get(0).getErrorMessage());
                } else {
                    // Putting multiple error messages at once is weird, just use the failure message.
                    return SafeResult.error(failureMessage);
                }
            }
            else {
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

        private SafeResult<T> defaultMultipleItemsHandler(InteractableKey<T> a, Set<T> b) {
            throw createMultipleInteractablesException(a, b);
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
