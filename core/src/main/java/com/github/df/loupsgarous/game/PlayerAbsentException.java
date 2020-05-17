package com.github.df.loupsgarous.game;

public class PlayerAbsentException extends RuntimeException {
    public PlayerAbsentException() {
    }

    public PlayerAbsentException(String message) {
        super(message);
    }

    public PlayerAbsentException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerAbsentException(Throwable cause) {
        super(cause);
    }
}
