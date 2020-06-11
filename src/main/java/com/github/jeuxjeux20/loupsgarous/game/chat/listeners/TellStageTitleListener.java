package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.events.stage.LGStageChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.importantInfo;

public class TellStageTitleListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onLGStageChanged(LGStageChangeEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        String title = event.getStage().getTitle();
        if (title != null) {
            orchestrator.chat().sendToEveryone(importantInfo(title));

            orchestrator.showSubtitle(title);
        }
    }
}
