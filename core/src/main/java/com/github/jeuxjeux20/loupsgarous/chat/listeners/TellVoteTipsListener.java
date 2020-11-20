package com.github.jeuxjeux20.loupsgarous.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseStartedEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.interaction.Interactable;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.Vote;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.chat.ComponentTemplates.VOTE_TIP;

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
