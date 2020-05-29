package com.github.jeuxjeux20.loupsgarous.game.lobby;

public class CannotCreateWorldException extends CannotCreateLobbyException {
    public CannotCreateWorldException() {
    }

    public CannotCreateWorldException(String message) {
        super(message);
    }

    public CannotCreateWorldException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotCreateWorldException(Throwable cause) {
        super(cause);
    }
}
