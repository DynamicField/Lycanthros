package com.github.jeuxjeux20.loupsgarous.game.lobby;

public class CannotCreateLobbyException extends Exception {
    public CannotCreateLobbyException() {
    }

    public CannotCreateLobbyException(String message) {
        super(message);
    }

    public CannotCreateLobbyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotCreateLobbyException(Throwable cause) {
        super(cause);
    }
}
