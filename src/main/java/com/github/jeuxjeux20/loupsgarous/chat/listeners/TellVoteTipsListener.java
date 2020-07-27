package com.github.jeuxjeux20.loupsgarous.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.event.stage.LGStageStartedEvent;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.Vote;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.ComponentTemplates.VOTE_TIP;

public class TellVoteTipsListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onLGStageStarted(LGStageStartedEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        for (Vote<?> vote : orchestrator.interactables().get(LGInteractableKeys.PLAYER_VOTE)) {
            orchestrator.chat().sendMessage(vote.getInfoMessagesChannel(), VOTE_TIP);
        }
    }
}
