package com.github.jeuxjeux20.loupsgarous.lobby;

public class WorldCreationException extends LobbyCreationException {
    public WorldCreationException() {
    }

    public WorldCreationException(String message) {
        super(message);
    }

    public WorldCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorldCreationException(Throwable cause) {
        super(cause);
    }
}
