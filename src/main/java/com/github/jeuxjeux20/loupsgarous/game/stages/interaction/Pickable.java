package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface Pickable extends PickableProvider {
    Check canPickTarget(LGPlayer target);

    Check canPlayerPick(LGPlayer picker);

    default Check canPick(LGPlayer picker, LGPlayer target) {
        return canPlayerPick(picker).and(() -> canPickTarget(target));
    }

    void pick(LGPlayer picker, LGPlayer target);

    @Override
    default Pickable providePickable() {
        return this;
    }
}
