package com.github.jeuxjeux20.loupsgarous.lobby;

public interface MultiverseWorldPool extends MultiverseWorldProvider, LobbyPresenceChecker {
    PooledMultiverseWorld get() throws WorldCreationException;
}
