package com.github.df.loupsgarous.lobby;

public interface MultiverseWorldPool extends MultiverseWorldProvider, LobbyPresenceChecker {
    PooledMultiverseWorld get() throws WorldCreationException;
}
