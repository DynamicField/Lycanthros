package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGGameChatManager;
import com.github.jeuxjeux20.loupsgarous.game.events.LGStageChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TellVoteTipsListener implements Listener {
    private final LGGameChatManager chatManager;

    @Inject
    public TellVoteTipsListener(LGGameChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLGStageChanged(LGStageChangeEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        for (Votable votable : event.getStage().getComponents(Votable.class)) {
            chatManager.sendVoteMessages(votable, orchestrator);
        }
    }
}
