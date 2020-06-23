package com.github.jeuxjeux20.loupsgarous.game.lobby;

public interface MultiverseWorldPool extends MultiverseWorldProvider, LobbyPresenceChecker {
    PooledMultiverseWorld get() throws WorldCreationException;
}
