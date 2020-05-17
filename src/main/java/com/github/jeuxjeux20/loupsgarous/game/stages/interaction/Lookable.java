package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface Lookable extends PickableProvider {
    Check canPlayerLook(LGPlayer looker);

    Check canLook(LGPlayer looker, LGPlayer target);

    void look(LGPlayer looker, LGPlayer target);

    @Override
    default Pickable providePickable() {
        return new Pickable() {
            @Override
            public Check canPlayerPick(LGPlayer picker) {
                return canPlayerLook(picker);
            }

            @Override
            public Check canPick(LGPlayer picker, LGPlayer target) {
                return canLook(picker, target);
            }

            @Override
            public void pick(LGPlayer picker, LGPlayer target) {
                look(picker, target);
            }
        };
    }
}
