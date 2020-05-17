package com.github.jeuxjeux20.loupsgarous.game.events;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGPickEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final PickableProvider pickableProvider;
    private final LGPlayer from;
    private final LGPlayer to;

    public LGPickEvent(LGGameOrchestrator orchestrator, PickableProvider pickableProvider,
                       LGPlayer from, LGPlayer to) {
        super(orchestrator);
        this.pickableProvider = pickableProvider;
        this.from = from;
        this.to = to;
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

    public LGPlayer getFrom() {
        return from;
    }

    public LGPlayer getTo() {
        return to;
    }
}
