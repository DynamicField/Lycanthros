package com.github.df.loupsgarous.chat.listeners;

import com.github.df.loupsgarous.event.phase.LGPhaseStartingEvent;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.github.df.loupsgarous.chat.LGChatStuff.importantInfo;

public class TellPhaseTitleListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLGPhaseStarted(LGPhaseStartingEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        String title = event.getPhase().getDescriptor().getTitle();
        if (title != null) {
            orchestrator.chat().sendToEveryone(importantInfo(title));

            orchestrator.getAllMinecraftPlayers()
                    .forEach(player -> player.sendTitle("", title, -1, -1, -1));
        }
    }
}
