package com.github.df.loupsgarous.interaction;

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
