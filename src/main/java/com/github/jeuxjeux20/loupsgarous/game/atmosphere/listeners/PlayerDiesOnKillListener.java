package com.github.jeuxjeux20.loupsgarous.game.atmosphere.listeners;

import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class PlayerDiesOnKillListener implements Listener {
    @EventHandler
    public void onLGKill(LGKillEvent event) {
        World world = event.getOrchestrator().world();
        for (LGKill kill : event.getKills()) {
            Optional<Player> maybePlayer = kill.getWhoDied().getMinecraftPlayer();

            Location location = maybePlayer.map(Entity::getLocation).orElse(world.getSpawnLocation());
            world.strikeLightningEffect(location);

            maybePlayer.ifPresent(player -> player.setHealth(0));
        }
    }
}