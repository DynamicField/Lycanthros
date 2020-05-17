package com.github.df.loupsgarous.game;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum LGGameState {
    LOBBY,
    STARTED,
    FINISHED,
    DELETING,
    DELETED;

    public boolean wentThrough(LGGameState state) {
        return this.compareTo(state) >= 0;
    }

    public boolean isEnabled() {
        return !isDisabled();
    }

    public boolean isDisabled() {
        return wentThrough(DELETING);
    }

    // According to RFC 2219 (lol)

    public void mustNotBe(LGGameState... states) {
        for (LGGameState state : states) {
            if (this == state) {
                throw new IllegalStateException(
                        "The game state (" + this + ") MUST NOT be in [" +
                        Arrays.stream(states).map(Object::toString)
                                .collect(Collectors.joining(", ")) +
                        "].");
            }
        }
    }

    public void mustNotBe(LGGameState state) {
        Preconditions.checkState(this != state,
                "The game state MUST NOT be: " + this.toString());
    }

    public void mustBe(LGGameState state) {
        Preconditions.checkState(this == state,
                "The game state MUST be: " + state.toString());
    }

    public void mustBe(LGGameState... states) {
        for (LGGameState state : states) {
            if (this == state) {
                return;
            }
        }

        throw new IllegalStateException(
                "The game state (" + this + ") MUST be in [" +
                Arrays.stream(states).map(Object::toString).collect(Collectors.joining(", ")) +
                "].");
    }

    public void mustBeActive() {
        if (isDisabled()) {
            throw new IllegalStateException("The game must not be DELETING or DELETED.");
        }
    }
}
