package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.base.MoreObjects;

final class MutableLGGameTurn implements LGGameTurn {
    private int turnNumber = 1;
    private LGGameTurnTime time = LGGameTurnTime.NIGHT;

    @Override
    public LGGameTurnTime getTime() {
        return time;
    }

    public void setTime(LGGameTurnTime time) {
        this.time = time;
    }

    @Override
    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("turnNumber", turnNumber)
                .add("time", time)
                .toString();
    }
}
