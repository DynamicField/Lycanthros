package com.github.jeuxjeux20.loupsgarous.game.lobby;

public interface MultiverseWorldPool extends MultiverseWorldProvider {
    PooledMultiverseWorld get() throws CannotCreateWorldException;
}
