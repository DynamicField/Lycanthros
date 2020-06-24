package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;

import java.util.Optional;

public abstract class LGPickEventBase<T, P extends Pickable<T>> extends LGEvent {
    protected final P pickable;
    protected final LGPlayer picker;
    protected final T target;

    public LGPickEventBase(LGGameOrchestrator orchestrator, P pickable,
                           LGPlayer picker, T target) {
        super(orchestrator);
        this.pickable = pickable;
        this.picker = picker;
        this.target = target;
    }

    // Here comes GENERIC BLACK MAGIC

    public abstract <NT, NP extends Pickable<NT>>
    Optional<? extends LGPickEventBase<NT, NP>> cast(Class<? extends NP> pickableClass);

    @SuppressWarnings("unchecked")
    protected final <NT,
            NP extends Pickable<NT>,
            E extends LGPickEventBase<NT, NP>>
    Optional<E> actualCast(Class<? extends NP> pickableProviderClass) {
        if (pickableProviderClass.isInstance(this.getPickable())) {
            return Optional.of((E) this);
        } else {
            return Optional.empty();
        }
    }


    public P getPickable() {
        return pickable;
    }

    public LGPlayer getPicker() {
        return picker;
    }

    public T getTarget() {
        return target;
    }
}
