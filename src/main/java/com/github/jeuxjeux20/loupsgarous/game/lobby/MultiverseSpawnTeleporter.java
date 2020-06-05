package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.google.inject.Inject;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.entity.Player;

public class MultiverseSpawnTeleporter implements SpawnTeleporter {
    private final MultiverseCore multiverse;

    @Inject
    public MultiverseSpawnTeleporter(MultiverseCore multiverse) {
        this.multiverse = multiverse;
    }

    @Override
    public void teleportToSpawn(Player player) {
        MultiverseWorld spawnWorld = multiverse.getMVWorldManager().getSpawnWorld();

        MVDestination destination = multiverse.getDestFactory().getDestination(spawnWorld.getName());

        multiverse.getSafeTTeleporter().teleport(player, player, destination);
    }
}
