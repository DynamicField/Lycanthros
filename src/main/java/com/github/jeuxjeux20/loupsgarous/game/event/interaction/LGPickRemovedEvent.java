package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.google.common.reflect.TypeToken;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class LGPickRemovedEvent<T, P extends Pickable<T>> extends LGPickEventBase<T, P> {
    private static final HandlerList handlerList = new HandlerList();

    private final boolean isInvalidate;

    public LGPickRemovedEvent(LGGameOrchestrator orchestrator,
                              P pickable,
                              LGPlayer picker, T target) {
        super(orchestrator, pickable, picker, target);
        isInvalidate = !pickable.conditions().checkPick(picker, target).isSuccess();
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public <NT, NP extends Pickable<NT>> Optional<LGPickRemovedEvent<NT, NP>> cast(Class<? extends NP> pickableClass) {
        return actualCast(pickableClass);
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
