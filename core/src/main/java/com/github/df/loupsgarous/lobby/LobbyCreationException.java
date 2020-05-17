package com.github.df.loupsgarous.lobby;

import com.github.df.loupsgarous.game.GameCreationException;

public class LobbyCreationException extends GameCreationException {
    public LobbyCreationException() {
    }

    public LobbyCreationException(String message) {
        super(message);
    }

    public LobbyCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LobbyCreationException(Throwable cause) {
        super(cause);
    }
}
