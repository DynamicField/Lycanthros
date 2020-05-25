package com.github.jeuxjeux20.loupsgarous.game.cards.composition;

public class IllegalPlayerCountException extends Exception {
    public IllegalPlayerCountException() {
    }

    public IllegalPlayerCountException(String message) {
        super(message);
    }

    public IllegalPlayerCountException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalPlayerCountException(Throwable cause) {
        super(cause);
    }
}
