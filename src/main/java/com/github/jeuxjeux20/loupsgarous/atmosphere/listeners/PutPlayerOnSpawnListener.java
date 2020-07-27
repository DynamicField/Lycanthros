package com.github.jeuxjeux20.loupsgarous.atmosphere.listeners;

import com.github.jeuxjeux20.loupsgarous.lobby.LobbyPresenceChecker;
import com.github.jeuxjeux20.loupsgarous.lobby.SpawnTeleporter;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This listener ensures that players that join the server are not in a lobby world,
 * and when it's the case, teleports them to the spawn.
 */
public class PutPlayerOnSpawnListener implements Listener {
    private final LobbyPresenceChecker lobbyPresenceChecker;
    private final SpawnTeleporter spawnTeleporter;

    @Inject
    PutPlayerOnSpawnListener(LobbyPresenceChecker lobbyPresenceChecker, SpawnTeleporter spawnTeleporter) {
        this.lobbyPresenceChecker = lobbyPresenceChecker;
        this.spawnTeleporter = spawnTeleporter;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (lobbyPresenceChecker.isInLobbyWorld(player)) {
            spawnTeleporter.teleportToSpawn(player);
        }
    }
}
