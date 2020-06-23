package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class LGPickEvent<T, PP extends PickableProvider<? extends Pickable<T>>> extends LGPickEventBase<T, PP> {
    private static final HandlerList handlerList = new HandlerList();

    public LGPickEvent(LGGameOrchestrator orchestrator, PP pickableProvider,
                       LGPlayer picker, T target) {
        super(orchestrator, pickableProvider, picker, target);
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public <NT, NPP extends PickableProvider<? extends Pickable<NT>>>
    Optional<LGPickEvent<NT, NPP>> cast(Class<? extends NPP> pickableProviderClass) {
        return actualCast(pickableProviderClass);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
