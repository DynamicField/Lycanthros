package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.interaction.NotifyingInteractable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.StatefulPickable;
import com.google.common.reflect.TypeToken;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class LGPickRemovedEvent<T, P extends StatefulPickable<T> & NotifyingInteractable> extends LGPickEventBase<T, P> {
    private static final HandlerList handlerList = new HandlerList();

    private final boolean isInvalidate;

    public LGPickRemovedEvent(LGGameOrchestrator orchestrator,
                              InteractableEntry<P> entry,
                              LGPlayer picker, T target) {
        super(orchestrator, entry, picker, target);
        isInvalidate = !entry.getValue().conditions().checkPick(picker, target).isSuccess();
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    public <NT, NP extends StatefulPickable<NT> & NotifyingInteractable>
    Optional<LGPickRemovedEvent<NT, NP>> cast(TypeToken<? extends NP> pickableProviderType) {
        return actualCast(pickableProviderType);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public boolean isInvalidate() {
        return isInvalidate;
    }

    public boolean isOrganic() {
        return !isInvalidate;
    }
}
