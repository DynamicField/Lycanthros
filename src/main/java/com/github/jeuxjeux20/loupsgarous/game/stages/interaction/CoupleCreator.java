package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface CoupleCreator extends PickableProvider<CouplePickable> {
    Check canCreateThatCouple(Couple couple);

    Check canPlayerCreateCouple(LGPlayer player);

    default Check canCreateCouple(LGPlayer player, Couple couple) {
        return canPlayerCreateCouple(player).and(() -> canCreateThatCouple(couple));
    }

    void createCouple(LGPlayer player, Couple couple);

    @Override
    default CouplePickable providePickable() {
        return new CouplePickable() {
            @Override
            public Check canPickTarget(Couple target) {
                return canCreateThatCouple(target);
            }

            @Override
            public Check canPlayerPick(LGPlayer picker) {
                return canPlayerCreateCouple(picker);
            }

            @Override
            public Check canPick(LGPlayer picker, Couple target) {
                return canCreateCouple(picker, target);
            }

            @Override
            public void pick(LGPlayer picker, Couple target) {
                createCouple(picker, target);
            }
        };
    }
}
