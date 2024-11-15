package com.github.df.loupsgarous.lobby;

import com.google.inject.Inject;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;

class MultiverseLobbyTeleporter implements LobbyTeleporter {
    private final MultiverseCore multiverse;
    private final TerminableMultiverseWorld terminableWorld;
    private final SpawnTeleporter spawnTeleporter;
    private final MultiverseWorld world;
    private final MVDestination worldDestination;

    @Inject
    MultiverseLobbyTeleporter(MultiverseCore multiverse, MultiverseWorldProvider multiverseWorldProvider,
                              SpawnTeleporter spawnTeleporter)
            throws WorldCreationException {
        this.multiverse = multiverse;

        this.terminableWorld = multiverseWorldProvider.get();
        this.spawnTeleporter = spawnTeleporter;
        this.world = terminableWorld.get();
        this.worldDestination = multiverse.getDestFactory().getDestination(world.getName());

        configureWorld();
    }

    @Override
    public World getWorld() {
        return world.getCBWorld();
    }

    @Override
    public void teleportPlayerIn(Player player) {
        if (player.getWorld() == world.getCBWorld()) return;
        multiverse.getSafeTTeleporter().teleport(player, player, worldDestination);
    }

    @Override
    public void teleportPlayerOut(Player player) {
        if (player.isDead()) {
            player.spigot().respawn();
        }
        spawnTeleporter.teleportToSpawn(player);
    }

    @Override
    public void close() {
        terminableWorld.closeAndReportException();
    }

    private void configureWorld() {
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameMode(GameMode.ADVENTURE);
        world.setAllowAnimalSpawn(false);
        world.setAllowMonsterSpawn(false);
        world.setTime("day");
        world.setPVPMode(false);
        world.setRespawnToWorld(getWorld().getName());
        world.getCBWorld().setGameRule(GameRule.FALL_DAMAGE, false);
        world.getCBWorld().setGameRule(GameRule.KEEP_INVENTORY, true);
        world.getCBWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
    }
}
