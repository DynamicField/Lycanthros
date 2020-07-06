package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import me.lucko.helper.terminable.TerminableConsumer;

public interface SelfBoundOrchestratorInteractableRegisterer
        extends InteractableRegisterer, TerminableConsumer, LGGameOrchestratorDependent {
    @Override
    default <T extends Interactable> void registerInteractable(InteractableKey<T> key, T value) {
        gameOrchestrator().interactables().put(key, bind(value));
    }

    @Override
    default void registerInteractable(SelfAwareInteractable interactable) {
        gameOrchestrator().interactables().put(bind(interactable));
    }
}
