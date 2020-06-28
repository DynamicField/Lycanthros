package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.interaction.NotifyingInteractable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Pickable;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import java.util.Optional;

public abstract class LGPickEventBase<T, P extends Pickable<T> & NotifyingInteractable> extends LGEvent {
    private final InteractableEntry<P> entry;
    protected final LGPlayer picker;
    protected final T target;

    public LGPickEventBase(LGGameOrchestrator orchestrator, InteractableEntry<P> entry,
                           LGPlayer picker, T target) {
        super(orchestrator);

        Preconditions.checkArgument(orchestrator.interactables().isPresent(entry),
                "The interactable entry " + entry + " is not present.");

        this.entry = entry;
        this.picker = picker;
        this.target = target;
    }

    // Here comes GENERIC BLACK MAGIC

    @SuppressWarnings("unchecked")
    protected final <NT,
            NP extends Pickable<NT> & NotifyingInteractable,
            E extends LGPickEventBase<NT, NP>>
    Optional<E> actualCast(TypeToken<? extends NP> pickableProviderType) {
        if (getEntry().getKey().getType().isSupertypeOf(pickableProviderType)) {
            return Optional.of((E) this);
        } else {
            return Optional.empty();
        }
    }

    public InteractableEntry<P> getEntry() {
        return entry;
    }

    public LGPlayer getPicker() {
        return picker;
    }

    public T getTarget() {
        return target;
    }
}
