package com.github.df.loupsgarous.lobby;

public class InvalidOwnerException extends LobbyCreationException {
    public InvalidOwnerException() {
    }

    public InvalidOwnerException(String message) {
        super(message);
    }

    public InvalidOwnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOwnerException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage() {
        if (getCause() != null) {
            return getCause().getLocalizedMessage();
        } else {
            return super.getLocalizedMessage();
        }
    }
}
