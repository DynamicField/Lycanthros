package com.github.df.loupsgarous.game;

public class GameCreationException extends Exception {
    public GameCreationException() {
    }

    public GameCreationException(String message) {
        super(message);
    }

    public GameCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameCreationException(Throwable cause) {
        super(cause);
    }
}
