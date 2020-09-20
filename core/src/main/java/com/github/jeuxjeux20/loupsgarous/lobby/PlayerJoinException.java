package com.github.jeuxjeux20.loupsgarous.lobby;

public class PlayerJoinException extends Exception {
    public PlayerJoinException() {
    }

    public PlayerJoinException(String message) {
        super(message);
    }

    public PlayerJoinException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerJoinException(Throwable cause) {
        super(cause);
    }
}
