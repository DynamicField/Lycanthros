package com.github.jeuxjeux20.loupsgarous.game.lobby;

public class CannotCloneWorldException extends CannotCreateWorldException {
    private final String worldName;

    public CannotCloneWorldException(String message, String worldName) {
        super(message);
        this.worldName = worldName;
    }

    public CannotCloneWorldException(String message, Throwable cause, String worldName) {
        super(message, cause);
        this.worldName = worldName;
    }

    public CannotCloneWorldException(Throwable cause, String worldName) {
        super(cause);
        this.worldName = worldName;
    }

    public String getWorldName() {
        return worldName;
    }
}
