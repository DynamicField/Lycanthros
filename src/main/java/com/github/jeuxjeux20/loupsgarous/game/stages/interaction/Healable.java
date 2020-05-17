package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface Healable extends PickableProvider {
    Check canPlayerHeal(LGPlayer healer);

    Check canHeal(LGPlayer healer, LGPlayer target);

    void heal(LGPlayer healer, LGPlayer target);

    @Override
    default Pickable providePickable() {
        return new Pickable() {
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
