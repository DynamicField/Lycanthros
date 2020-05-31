package com.github.jeuxjeux20.loupsgarous.signs.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.util.DefaultCompositions;
import com.github.jeuxjeux20.loupsgarous.signs.GameJoinSignManager;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.inject.Inject;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.Optional;

public class JoinOnSignClickListener implements Listener {
    private final GameJoinSignManager signManager;
    private final LGGameManager gameManager;

    @Inject
    JoinOnSignClickListener(GameJoinSignManager signManager, LGGameManager gameManager) {
        this.signManager = signManager;
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null ||
            !(event.getClickedBlock().getState() instanceof Sign)) return;

        Sign sign = (Sign) event.getClickedBlock().getState();

        Optional<String> name = signManager.getSignGameName(sign);

        Optional<LGGameOrchestrator> maybeGame = OptionalUtils.or(
                () -> name.flatMap(gameManager::getGameById),
                () -> name.flatMap(n ->
                        gameManager.startGame(
                                Collections.singleton(event.getPlayer()),
                                DefaultCompositions.villagerComposition(8), n).getValueOptional())
        );
        maybeGame.ifPresent(game -> game.lobby().addPlayer(event.getPlayer()));
    }
}
