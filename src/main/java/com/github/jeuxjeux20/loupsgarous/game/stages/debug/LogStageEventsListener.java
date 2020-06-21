package com.github.jeuxjeux20.loupsgarous.game.stages.debug;

import com.github.jeuxjeux20.loupsgarous.Plugin;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.*;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class LogStageEventsListener implements Listener {
    @SuppressWarnings("unchecked")
    private static final Class<? extends LGStageEvent>[] allEvents = (Class<? extends LGStageEvent>[])
            new Class[]{
                    LGStageStartingEvent.class,
                    LGStageStartedEvent.class,
                    LGStageEndingEvent.class,
                    LGStageEndedEvent.class
            };

    private final Logger logger;

    @Inject
    LogStageEventsListener(@Plugin Logger logger) {
        this.logger = logger;
        Events.merge(LGStageEvent.class, EventPriority.MONITOR, allEvents)
                .handler(this::logStageEvent);
    }

    private void logStageEvent(LGStageEvent e) {
        String outcome = (e instanceof Cancellable && ((Cancellable) e).isCancelled()) ?
                "cancelled" : "completed";
        this.logger.fine(e.getEventName() + " " + outcome + " for " + e.getStage().getClass().getSimpleName());
    }
}
