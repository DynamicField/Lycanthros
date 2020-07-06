package com.github.jeuxjeux20.loupsgarous.game.interaction;

public interface InteractableRegisterer {
    <T extends Interactable> void registerInteractable(InteractableKey<T> key, T value);

    void registerInteractable(SelfAwareInteractable interactable);
}
