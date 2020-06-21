package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface Pickable<T> extends PickableProvider<Pickable<T>> {
    Check canPickTarget(T target);

    Check canPlayerPick(LGPlayer picker);

    Check canPick(LGPlayer picker, T target);

    void pick(LGPlayer picker, T target);

    @Override
    default Pickable<T> providePickable() {
        return this;
    }
}
