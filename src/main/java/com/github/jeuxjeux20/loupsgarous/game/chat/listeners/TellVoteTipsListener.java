package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageStartedEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.ComponentTemplates.VOTE_TIP;

public class TellVoteTipsListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onLGStageStarted(LGStageStartedEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        for (Votable votable : event.getStage().getComponents(Votable.class)) {
            orchestrator.chat().sendMessage(votable.getInfoMessagesChannel(), VOTE_TIP);
        }
    }
}
