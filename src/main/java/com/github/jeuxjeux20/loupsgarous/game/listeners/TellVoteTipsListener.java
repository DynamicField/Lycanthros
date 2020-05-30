package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatManager;
import com.github.jeuxjeux20.loupsgarous.game.events.stage.LGStageChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TellVoteTipsListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onLGStageChange(LGStageChangeEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        for (Votable votable : event.getStage().getComponents(Votable.class)) {
            orchestrator.chat().sendVoteMessages(votable);
        }
    }
}
