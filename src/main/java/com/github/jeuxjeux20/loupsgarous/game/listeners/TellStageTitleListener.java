package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.events.LGStageChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.importantInfo;

public class TellStageTitleListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onLGStageChanged(LGStageChangeEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        event.getStage().getTitle().ifPresent(title -> {
            orchestrator.sendToEveryone(importantInfo(title));

            orchestrator.showSubtitle(title);
        });
    }
}
