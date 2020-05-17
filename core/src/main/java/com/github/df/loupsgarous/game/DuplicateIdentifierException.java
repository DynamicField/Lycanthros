package com.github.df.loupsgarous.game;

public class DuplicateIdentifierException extends GameCreationException {
    public DuplicateIdentifierException() {
    }

    public DuplicateIdentifierException(String message) {
        super(message);
    }

    public DuplicateIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateIdentifierException(Throwable cause) {
        super(cause);
    }
}
