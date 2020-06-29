package com.github.jeuxjeux20.loupsgarous.game.interaction;

import me.lucko.helper.terminable.Terminable;

/**
 * The base interface for interactable objects.
 */
public interface Interactable extends Terminable {
    /**
     * {@inheritDoc}
     */
    @Override
    boolean isClosed();
}
