package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface Healable extends PickableProvider<PlayerPickable> {
    Check canHealTarget(LGPlayer target);

    Check canPlayerHeal(LGPlayer healer);

    default Check canHeal(LGPlayer healer, LGPlayer target) {
        return canPlayerHeal(healer).and(() -> canHealTarget(target));
    }

    void heal(LGPlayer healer, LGPlayer target);

    @Override
    default PlayerPickable providePickable() {
        return new PlayerPickable() {
            @Override
            public Check canPickTarget(LGPlayer target) {
                return canHealTarget(target);
            }

            @Override
            public Check canPlayerPick(LGPlayer picker) {
                return canPlayerHeal(picker);
            }

            @Override
            public Check canPick(LGPlayer picker, LGPlayer target) {
                return canHeal(picker, target);
            }

            @Override
            public void pick(LGPlayer picker, LGPlayer target) {
                heal(picker, target);
            }
        };
    }
}
