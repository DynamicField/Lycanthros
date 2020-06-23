package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;

import java.util.Optional;

public abstract class LGPickEventBase<T, PP extends PickableProvider<? extends Pickable<T>>> extends LGEvent {
    protected final PP pickableProvider;
    protected final LGPlayer picker;
    protected final T target;

    public LGPickEventBase(LGGameOrchestrator orchestrator, PP pickableProvider,
                           LGPlayer picker, T target) {
        super(orchestrator);
        this.pickableProvider = pickableProvider;
        this.picker = picker;
        this.target = target;
    }

    // Here comes GENERIC BLACK MAGIC

    public abstract <NT, NPP extends PickableProvider<? extends Pickable<NT>>>
    Optional<? extends LGPickEventBase<NT, NPP>> cast(Class<? extends NPP> pickableProviderClass);

    @SuppressWarnings("unchecked")
    protected final <NT,
            NPP extends PickableProvider<? extends Pickable<NT>>,
            E extends LGPickEventBase<NT, NPP>>
    Optional<E> actualCast(Class<? extends NPP> pickableProviderClass) {
        if (pickableProviderClass.isInstance(this.getPickableProvider())) {
            return Optional.of((E) this);
        } else {
            return Optional.empty();
        }
    }


    public PP getPickableProvider() {
        return pickableProvider;
    }

    public LGPlayer getPicker() {
        return picker;
    }

    public T getTarget() {
        return target;
    }
}
