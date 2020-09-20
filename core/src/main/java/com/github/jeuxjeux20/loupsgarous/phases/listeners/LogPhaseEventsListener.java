package com.github.jeuxjeux20.loupsgarous.phases.listeners;

import com.github.jeuxjeux20.loupsgarous.event.phase.*;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LogPhaseEventsListener implements Listener {
    @SuppressWarnings("unchecked")
    private static final Class<? extends LGPhaseEvent>[] allEvents = (Class<? extends LGPhaseEvent>[])
            new Class[]{
                    LGPhaseStartingEvent.class,
                    LGPhaseStartedEvent.class,
                    LGPhaseEndingEvent.class,
                    LGPhaseEndedEvent.class
            };

    @Inject
    LogPhaseEventsListener() {
        Events.merge(LGPhaseEvent.class, EventPriority.MONITOR, allEvents)
                .handler(this::logPhaseEvent);
    }

    private void logPhaseEvent(LGPhaseEvent e) {
        String outcome = (e instanceof Cancellable && ((Cancellable) e).isCancelled()) ?
                "cancelled" : "completed";

        String phaseName = e.getPhase().getClass().getSimpleName();
        e.getOrchestrator().logger().finer(e.getEventName() + " " + outcome + " for " + phaseName);
    }
}
