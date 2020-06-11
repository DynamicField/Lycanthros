package com.github.jeuxjeux20.loupsgarous.game.events;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGPickRemovedEvent extends LGPickEvent {
    private static final HandlerList handlerList = new HandlerList();

    public LGPickRemovedEvent(LGGameOrchestrator orchestrator, PickableProvider pickableProvider,
                              LGPlayer picker, LGPlayer target) {
        super(orchestrator, pickableProvider, picker, target);
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
