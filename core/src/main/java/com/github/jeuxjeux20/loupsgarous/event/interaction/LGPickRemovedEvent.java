package com.github.jeuxjeux20.loupsgarous.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.interaction.PickData;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class LGPickRemovedEvent extends LGPickEventBase {
    private static final HandlerList handlerList = new HandlerList();

    private final boolean isInvalidate;

    public LGPickRemovedEvent(LGGameOrchestrator orchestrator, PickData<?, ?> pickData, boolean isInvalidate) {
        super(orchestrator, pickData);
        this.isInvalidate = isInvalidate;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
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
