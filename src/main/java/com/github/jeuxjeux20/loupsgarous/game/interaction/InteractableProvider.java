package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

import java.util.Set;

public interface InteractableProvider {
    Set<InteractableEntry<?>> getInteractables();

    default <T extends Interactable> ImmutableSet<InteractableEntry<T>> getInteractables(Class<T> clazz) {
        return getInteractables(TypeToken.of(clazz));
    }

    @SuppressWarnings("unchecked")
    default <T extends Interactable> ImmutableSet<InteractableEntry<T>> getInteractables(TypeToken<T> type) {
        // Just let me cast!!!
        return (ImmutableSet<InteractableEntry<T>>) (Object) getInteractables().stream()
                .filter(x -> type.isSupertypeOf(x.getKey().getType()))
                .collect(ImmutableSet.toImmutableSet());
    }
}
