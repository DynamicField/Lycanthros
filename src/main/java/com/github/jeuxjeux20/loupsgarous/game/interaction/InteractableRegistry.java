package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.util.CheckPredicate;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface InteractableRegistry extends OrchestratorComponent {
    <T extends Interactable> ImmutableSet<T> get(InteractableKey<T> key);

    <T extends Interactable> SafeSingleBuilder<T> single(InteractableKey<T> key);

    <T extends Interactable> boolean register(InteractableKey<? super T> key, T value);

    <T extends Interactable> boolean remove(InteractableKey<? super T> key, T value);

    Optional<InteractableKey<?>> findKey(String name);

    ImmutableSetMultimap<InteractableKey<?>, Interactable> getAll();

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
