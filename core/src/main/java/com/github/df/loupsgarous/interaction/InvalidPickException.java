package com.github.df.loupsgarous.interaction;

public class InvalidPickException extends RuntimeException {
    public InvalidPickException() {
    }

    public InvalidPickException(String message) {
        super(message);
    }

    public InvalidPickException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPickException(Throwable cause) {
        super(cause);
    }
}
