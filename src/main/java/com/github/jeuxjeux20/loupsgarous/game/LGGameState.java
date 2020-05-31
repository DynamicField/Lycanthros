package com.github.jeuxjeux20.loupsgarous.game;

public enum LGGameState {
    UNINITIALIZED,
    WAITING_FOR_PLAYERS,
    READY_TO_START,
    STARTED,
    FINISHED,
    DELETING,
    DELETED;

    public boolean wentThrough(LGGameState state) {
        return this.compareTo(state) >= 0;
    }

    public boolean didNotPass(LGGameState state) {
        return this.compareTo(state) < 0;
    }

    public boolean isEnabled() {
        return !isDisabled();
    }

    public boolean isDisabled() {
        return wentThrough(DELETING);
    }
}
