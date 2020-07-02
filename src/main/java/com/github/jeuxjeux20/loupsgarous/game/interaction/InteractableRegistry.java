package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import com.github.jeuxjeux20.loupsgarous.util.CheckPredicate;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface InteractableRegistry extends Terminable, LGGameOrchestratorDependent {
    <T extends Interactable> ImmutableSet<T> get(InteractableKey<T> key);

    <T extends Interactable> SafeSingleBuilder<T> single(InteractableKey<T> key);


    <T extends Interactable> boolean put(InteractableKey<T> key, T value);

    default <T extends Interactable> boolean put(InteractableEntry<T> entry) {
        return put(entry.getKey(), entry.getValue());
    }

    default boolean put(SelfAwareInteractable interactable) {
        return put(interactable.getEntry());
    }


    <T extends Interactable> boolean remove(InteractableKey<T> key, T value);

    default <T extends Interactable> boolean remove(InteractableEntry<T> entry) {
        return remove(entry.getKey(), entry.getValue());
    }

    default boolean remove(SelfAwareInteractable interactable) {
        return remove(interactable.getEntry());
    }


    <T extends Interactable> boolean has(InteractableKey<T> key, T value);

    default <T extends Interactable> boolean has(InteractableEntry<T> entry) {
        return has(entry.getKey(), entry.getValue());
    }

    default boolean has(SelfAwareInteractable interactable) {
        return has(interactable.getEntry());
    }

    Optional<InteractableKey<?>> findKey(String name);

    ImmutableSetMultimap<InteractableKey<?>, Interactable> getAll();


    interface Factory {
        InteractableRegistry create(LGGameOrchestrator orchestrator);
    }

    interface SafeSingleBuilder<T extends Interactable> {
        SafeSingleBuilder<T> check(CheckPredicate<? super T> checkPredicate);

        SafeSingleBuilder<T> failureMessage(String failureMessage);

        SafeSingleBuilder<T> multipleItemsHandler(MultipleItemsHandler<T> handler);


        SafeResult<T> get();

        default Optional<T> getOptional() {
            return get().getValueOptional();
        }

        default @Nullable T getOrNull() {
            return getOptional().orElse(null);
        }

        interface MultipleItemsHandler<T extends Interactable> {
            SafeResult<T> handle(InteractableKey<T> key, Set<T> interactables);
        }
    }
}
