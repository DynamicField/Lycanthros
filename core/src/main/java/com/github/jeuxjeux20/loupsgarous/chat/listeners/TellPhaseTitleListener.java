package com.github.jeuxjeux20.loupsgarous.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseStartingEvent;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhaseDescriptor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.importantInfo;

public class TellPhaseTitleListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLGPhaseStarted(LGPhaseStartingEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        LGPhase phase = event.getPhase();
        LGPhaseDescriptor descriptor = orchestrator.phases().descriptors().get(phase.getClass());

        String title = descriptor.getTitle();
        if (title != null) {
            orchestrator.chat().sendToEveryone(importantInfo(title));

            orchestrator.getAllMinecraftPlayers()
                    .forEach(player -> player.sendTitle("", title, -1, -1, -1));
        }
    }
}
