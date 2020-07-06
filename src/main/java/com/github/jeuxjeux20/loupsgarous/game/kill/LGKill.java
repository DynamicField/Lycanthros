package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.LGKillReason;

import java.util.Objects;

public final class LGKill {
    private final LGPlayer player;
    private final LGKillReason reason;

    public LGKill(LGPlayer player, LGKillReason reason) {
        this.player = Objects.requireNonNull(player, "player is null");
        this.reason = Objects.requireNonNull(reason, "reason is null");
    }

    public static LGKill of(LGPlayer player, LGKillReason reason) {
        return new LGKill(player, reason);
    }

    public LGPlayer getWhoDied() {
        return player;
    }

    public LGKillReason getReason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LGKill lgKill = (LGKill) o;
        return player.equals(lgKill.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
