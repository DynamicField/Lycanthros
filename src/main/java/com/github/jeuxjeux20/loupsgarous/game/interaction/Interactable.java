package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import me.lucko.helper.terminable.Terminable;

/**
 * The base interface for interactable objects.
 * <p>
 * Interactables have the following properties:
 * <ul>
 *     <li><b>Context awareness.</b> They must be aware of their game orchestrator,
 *     which is why they are {@link LGGameOrchestratorDependent}.</li>
 *     <li><b>Disposability.</b> Interactables are {@link Terminable} and can be closed.
 *     Once they are closed, {@link #isClosed()} must <b>return {@code true}</b>, and
 *     any state must be <b>unmodifiable</b>.</li>
 * </ul>
 *
 * @see AbstractInteractable
 */
public interface Interactable extends Terminable, LGGameOrchestratorDependent {
    /**
     * {@inheritDoc}
     */
    @Override
    boolean isClosed();
}
