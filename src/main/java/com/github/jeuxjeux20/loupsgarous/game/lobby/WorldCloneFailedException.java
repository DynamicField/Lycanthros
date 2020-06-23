package com.github.jeuxjeux20.loupsgarous.game.lobby;

public class WorldCloneFailedException extends WorldCreationException {
    private final String worldName;

    public WorldCloneFailedException(String message, String worldName) {
        super(message);
        this.worldName = worldName;
    }

    public WorldCloneFailedException(String message, Throwable cause, String worldName) {
        super(message, cause);
        this.worldName = worldName;
    }

    public WorldCloneFailedException(Throwable cause, String worldName) {
        super(cause);
        this.worldName = worldName;
    }

    public String getWorldName() {
        return worldName;
    }
}
