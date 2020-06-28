package com.github.jeuxjeux20.loupsgarous.game.interaction;

public interface SelfKeyedInteractable<S extends SelfKeyedInteractable<S>> extends Interactable {
    InteractableKey<? super S> getKey();
}
