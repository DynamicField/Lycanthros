package com.github.jeuxjeux20.loupsgarous.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.event.stage.LGStageStartingEvent;
import com.github.jeuxjeux20.loupsgarous.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.stages.descriptor.LGStageDescriptor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.importantInfo;

public class TellStageTitleListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLGStageStarted(LGStageStartingEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        LGStage stage = event.getStage();
        LGStageDescriptor descriptor = orchestrator.stages().descriptors().get(stage.getClass());

        String title = descriptor.getTitle();
        if (title != null) {
            orchestrator.chat().sendToEveryone(importantInfo(title));

            orchestrator.showSubtitle(title);
        }
    }
}
