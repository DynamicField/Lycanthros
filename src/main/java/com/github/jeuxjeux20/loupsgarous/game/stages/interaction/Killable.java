package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface Killable extends PickableProvider<PlayerPickable> {
    default Check canKillTarget(LGPlayer target) {
        return Check.ensure(target.isAlive(), "Impossible de tuer un joueur mort.");
    }

    Check canPlayerKill(LGPlayer killer);

    default Check canKill(LGPlayer killer, LGPlayer target) {
        return canPlayerKill(killer)
                .and(() -> canKillTarget(target));
    }

    void kill(LGPlayer killer, LGPlayer target);

    @Override
    default PlayerPickable providePickable() {
        return new PlayerPickable() {
            @Override
            public Check canPickTarget(LGPlayer target) {
                return canKillTarget(target);
            }

            @Override
            public Check canPlayerPick(LGPlayer picker) {
                return canPlayerKill(picker);
            }

            @Override
            public Check canPick(LGPlayer picker, LGPlayer target) {
                return canKill(picker, target);
            }

            @Override
            public void pick(LGPlayer picker, LGPlayer target) {
                kill(picker, target);
            }
        };
    }
}
