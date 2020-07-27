package com.github.jeuxjeux20.loupsgarous.signs.listeners;

import com.github.jeuxjeux20.loupsgarous.game.GameCreationException;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.cards.composition.util.DefaultCompositions;
import com.github.jeuxjeux20.loupsgarous.lobby.PlayerJoinException;
import com.github.jeuxjeux20.loupsgarous.signs.GameJoinSignManager;
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
        Optional<String> maybeName = signManager.getSignGameName(sign);

        if (!maybeName.isPresent() || !cooldownMap.test(player.getUniqueId())) {
            return;
        }

        String name = maybeName.get();

        Schedulers.sync().runLater(() -> {
            try {
                gameManager.joinOrStart(player, DefaultCompositions.villagerComposition(8), name);
            } catch (GameCreationException e) {
                player.sendMessage(ChatColor.RED + "Impossible de créer la partie: " + e.getLocalizedMessage());
            } catch (PlayerJoinException e) {
                player.sendMessage(ChatColor.RED + "Impossible de rejoindre la partie: " + e.getLocalizedMessage());
            }
        }, 4); // Wait 4 ticks so the click action doesn't occur again.
    }
}
