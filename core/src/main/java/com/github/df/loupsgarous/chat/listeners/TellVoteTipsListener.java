package com.github.df.loupsgarous.chat.listeners;

import com.github.df.loupsgarous.event.phase.LGPhaseStartedEvent;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.interaction.Interactable;
import com.github.df.loupsgarous.interaction.LGInteractableKeys;
import com.github.df.loupsgarous.interaction.vote.Vote;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.df.loupsgarous.chat.ComponentTemplates.VOTE_TIP;

public class TellVoteTipsListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onLGPhaseStarted(LGPhaseStartedEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        for (Interactable interactable : orchestrator.interactables().get(LGInteractableKeys.PLAYER_VOTE)) {
            if (interactable instanceof Vote<?>) {
                orchestrator.chat().sendMessage(
                        ((Vote<?>)interactable).getInfoMessagesChannel(), VOTE_TIP);
            }
        }
    }
}
