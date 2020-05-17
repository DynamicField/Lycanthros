package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface Pickable extends PickableProvider {
    Check canPlayerPick(LGPlayer picker);

    Check canPick(LGPlayer picker, LGPlayer target);

    void pick(LGPlayer picker, LGPlayer target);

    @Override
    default Pickable providePickable() {
        return this;
    }
}
