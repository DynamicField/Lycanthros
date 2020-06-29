package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public abstract class AbstractPickable<T> extends AbstractInteractable implements Pickable<T> {
    @Override
    public final void pick(LGPlayer picker, T target) {
        throwIfClosed();
        conditions().throwIfInvalid(picker, target);

        safePick(picker, target);
    }

    protected abstract void safePick(LGPlayer picker, T target);
}
