package com.github.jeuxjeux20.loupsgarous.game.events;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGPickEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final PickableProvider pickableProvider;
    private final LGPlayer picker;
    private final LGPlayer target;

    public LGPickEvent(LGGameOrchestrator orchestrator, PickableProvider pickableProvider,
                       LGPlayer picker, LGPlayer target) {
        super(orchestrator);
        this.pickableProvider = pickableProvider;
        this.picker = picker;
        this.target = target;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public PickableProvider getPickableProvider() {
        return pickableProvider;
    }

    public LGPlayer getPicker() {
        return picker;
    }

    public LGPlayer getTarget() {
        return target;
    }
}
