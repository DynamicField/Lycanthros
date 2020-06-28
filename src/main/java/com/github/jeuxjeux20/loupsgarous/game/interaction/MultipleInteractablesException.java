package com.github.jeuxjeux20.loupsgarous.game.interaction;

public class MultipleInteractablesException extends RuntimeException {
    public MultipleInteractablesException() {
    }

    public MultipleInteractablesException(String message) {
        super(message);
    }

    public MultipleInteractablesException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleInteractablesException(Throwable cause) {
        super(cause);
    }
}
