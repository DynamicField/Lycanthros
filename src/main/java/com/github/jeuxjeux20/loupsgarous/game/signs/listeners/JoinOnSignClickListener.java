package com.github.jeuxjeux20.loupsgarous.game.signs.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.util.DefaultCompositions;
import com.github.jeuxjeux20.loupsgarous.game.signs.GameJoinSignManager;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.inject.Inject;
import me.lucko.helper.Schedulers;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class JoinOnSignClickListener implements Listener {
    private final GameJoinSignManager signManager;
    private final LGGameManager gameManager;

    private final CooldownMap<UUID> cooldownMap = CooldownMap.create(Cooldown.ofTicks(5));

    @Inject
    JoinOnSignClickListener(GameJoinSignManager signManager, LGGameManager gameManager) {
        this.signManager = signManager;
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE ||
            event.getClickedBlock() == null ||
            !(event.getClickedBlock().getState() instanceof Sign)) return;

        Player player = event.getPlayer();

        Sign sign = (Sign) event.getClickedBlock().getState();

        Optional<String> name = signManager.getSignGameName(sign);

        if (!name.isPresent() || !cooldownMap.test(player.getUniqueId())) {
            return;
        }

        Schedulers.sync().runLater(() -> {
            Optional<LGGameOrchestrator> maybeGame = OptionalUtils.or(
                    () -> name.flatMap(gameManager::getGameById),
                    () -> name.flatMap(n ->
                            gameManager.startGame(
                                    Collections.singleton(player),
                                    DefaultCompositions.villagerComposition(8), n).getValueOptional())
            );

            boolean joined;
            if (gameManager.getPlayerInGame(player).isPresent()) {
                // The player is in a game? great!
                joined = true;
            }
            else {
                joined = maybeGame.map(x -> x.lobby().addPlayer(player)).orElse(false);
            }

            if (!joined) {
                player.sendMessage(ChatColor.RED + "Impossible de rejoindre la partie.");
            }
        }, 4); // Wait 4 ticks so the click action doesn't occur again.
    }
}
