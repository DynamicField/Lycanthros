package com.github.jeuxjeux20.loupsgarous;

public class MultipleComponentsException extends RuntimeException {
    public MultipleComponentsException() {
    }

    public MultipleComponentsException(String message) {
        super(message);
    }

    public MultipleComponentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleComponentsException(Throwable cause) {
        super(cause);
    }
}
