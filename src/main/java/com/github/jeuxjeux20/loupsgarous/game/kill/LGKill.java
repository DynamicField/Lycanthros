package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.LGKillReason;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.util.function.Supplier;

public final class LGKill {
    private final LGPlayer player;
    private final LGKillReason reason;

    public LGKill(LGPlayer player, LGKillReason reason) {
        Preconditions.checkArgument(player.isAlive(), "Cannot create a LGKill when the player is already dead");

        this.player = player;
        this.reason = reason;
    }

    public static LGKill of(LGPlayer player, LGKillReason reason) {
        return new LGKill(player, reason);
    }

    public static LGKill of(LGPlayer player, Supplier<? extends LGKillReason> reasonSupplier) {
        return new LGKill(player, reasonSupplier.get());
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
        return Objects.equal(player, lgKill.player);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player);
    }
}
