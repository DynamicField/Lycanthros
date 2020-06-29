package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;

public abstract class AbstractPickable<T> extends AbstractInteractable implements Pickable<T> {
    @Override
    public final void pick(LGPlayer picker, T target) {
        throwIfClosed();
        conditions().throwIfInvalid(picker, target);

        safePick(picker, target);
    }

    @Override
    public final PickConditions<T> conditions() {
        return FunctionalPickConditions.<T>builder()
                .use(criticalConditions())
                .use(pickConditions())
                .build();
    }

    protected final PickConditions<T> criticalConditions() {
        return FunctionalPickConditions.<T>builder()
                .ensurePicker(LGPlayer::isPresent, "Vous n'êtes pas dans la partie.")
                .build();
    }

    protected abstract PickConditions<T> pickConditions();

    protected abstract void safePick(LGPlayer picker, T target);
}
