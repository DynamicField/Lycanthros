package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.google.inject.Inject;
import me.lucko.helper.Schedulers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class RedirectChatMessageListener implements Listener {
    private final LGGameManager gameManager;

    @Inject
    RedirectChatMessageListener(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChatMessage(AsyncPlayerChatEvent event) {
        Optional<LGPlayerAndGame> playerAndGame = gameManager.getPlayerInGame(event.getPlayer());
        if (!playerAndGame.isPresent()) return;

        event.setCancelled(true);

        LGPlayerAndGame data = playerAndGame.get();

        Schedulers.sync().run(() -> {
            LGPlayer sender = data.getPlayer();
            LGGameOrchestrator orchestrator = data.getOrchestrator();

            orchestrator.chat().redirectMessage(sender, event.getMessage(), event.getFormat());
        });
    }
}
