package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class LGPickRemovedEvent<T, PP extends PickableProvider<? extends Pickable<T>>> extends LGPickEventBase<T, PP> {
    private static final HandlerList handlerList = new HandlerList();

    private final boolean isInvalidate;

    public LGPickRemovedEvent(LGGameOrchestrator orchestrator,
                              PP pickableProvider,
                              LGPlayer picker, T target) {
        super(orchestrator, pickableProvider, picker, target);
        isInvalidate = !pickableProvider.providePickable().canPick(picker, target).isSuccess();
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public <NT, NPP extends PickableProvider<? extends Pickable<NT>>>
    Optional<LGPickRemovedEvent<NT, NPP>> cast(Class<? extends NPP> pickableProviderClass) {
        return actualCast(pickableProviderClass);
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
