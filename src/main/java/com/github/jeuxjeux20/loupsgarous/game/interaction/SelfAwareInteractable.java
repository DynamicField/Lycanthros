package com.github.jeuxjeux20.loupsgarous.game.interaction;

public interface SelfAwareInteractable<E extends Interactable> extends Interactable {
    InteractableEntry<E> getEntry();
}
