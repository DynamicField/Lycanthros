package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface Lookable extends PickableProvider {
    Check canLookTarget(LGPlayer target);

    Check canPlayerLook(LGPlayer looker);

    default Check canLook(LGPlayer looker, LGPlayer target) {
        return canPlayerLook(looker).and(() -> canLookTarget(target));
    }

    void look(LGPlayer looker, LGPlayer target);

    @Override
    default Pickable providePickable() {
        return new Pickable() {
            @Override
            public Check canPickTarget(LGPlayer target) {
                return canLookTarget(target);
            }

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
