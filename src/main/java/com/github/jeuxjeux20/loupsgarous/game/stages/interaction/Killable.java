package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface Killable extends PickableProvider {
    Check canPlayerKill(LGPlayer killer);

    default Check canKill(LGPlayer killer, LGPlayer target) {
        return canPlayerKill(killer).and(target.isAlive(), "Impossible de tuer un joueur mort.");
    }

    void kill(LGPlayer killer, LGPlayer target);

    @Override
    default Pickable providePickable() {
        return new Pickable() {
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
