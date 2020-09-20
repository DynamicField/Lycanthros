package com.github.jeuxjeux20.loupsgarous.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.kill.causes.LGKillCause;

import java.util.Objects;

public final class LGKill {
    private final LGPlayer victim;
    private final LGKillCause cause;

    public LGKill(LGPlayer victim, LGKillCause cause) {
        this.victim = Objects.requireNonNull(victim, "player is null");
        this.cause = Objects.requireNonNull(cause, "reason is null");
    }

    public static LGKill of(LGPlayer player, LGKillCause reason) {
        return new LGKill(player, reason);
    }

    public boolean canTakeEffect() {
        return getVictim().isAlive();
    }

    public LGPlayer getVictim() {
        return victim;
    }

    public LGKillCause getCause() {
        return cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LGKill lgKill = (LGKill) o;
        return victim.equals(lgKill.victim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(victim);
    }
}
