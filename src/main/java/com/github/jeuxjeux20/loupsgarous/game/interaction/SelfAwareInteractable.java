package com.github.jeuxjeux20.loupsgarous.game.interaction;

public interface SelfAwareInteractable extends Interactable {
    InteractableEntry<?> getEntry();
}
