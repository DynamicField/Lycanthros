package com.github.jeuxjeux20.loupsgarous.game.stages.listeners;

import com.github.jeuxjeux20.loupsgarous.game.event.stage.*;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LogStageEventsListener implements Listener {
    @SuppressWarnings("unchecked")
    private static final Class<? extends LGStageEvent>[] allEvents = (Class<? extends LGStageEvent>[])
            new Class[]{
                    LGStageStartingEvent.class,
                    LGStageStartedEvent.class,
                    LGStageEndingEvent.class,
                    LGStageEndedEvent.class
            };

    @Inject
    LogStageEventsListener() {
        Events.merge(LGStageEvent.class, EventPriority.MONITOR, allEvents)
                .handler(this::logStageEvent);
    }

    private void logStageEvent(LGStageEvent e) {
        String outcome = (e instanceof Cancellable && ((Cancellable) e).isCancelled()) ?
                "cancelled" : "completed";

        String stageName = e.getStage().getClass().getSimpleName();
        e.getOrchestrator().logger().finer(e.getEventName() + " " + outcome + " for " + stageName);
    }
}
