package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGGameChatManager;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class RedirectChatMessageListener implements Listener {
    private final LGGameManager gameManager;
    private final LGGameChatManager chatManager;

    @Inject
    RedirectChatMessageListener(LGGameManager gameManager, LGGameChatManager chatManager) {
        this.gameManager = gameManager;
        this.chatManager = chatManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChatMessage(AsyncPlayerChatEvent event) {
        Optional<LGPlayerAndGame> playerAndGame = gameManager.getPlayerInGame(event.getPlayer());
        if (!playerAndGame.isPresent()) return;

        LGPlayer sender = playerAndGame.get().getPlayer();
        LGGameOrchestrator orchestrator = playerAndGame.get().getOrchestrator();

        event.setCancelled(true);
        chatManager.redirectMessage(sender, event.getMessage(), orchestrator);
    }
}
