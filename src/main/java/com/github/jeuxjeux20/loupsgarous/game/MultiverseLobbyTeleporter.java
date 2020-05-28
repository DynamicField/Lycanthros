package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.config.LGConfiguration;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MultiverseLobbyTeleporter implements LobbyTeleporter {
    private final MultiverseCore multiverse;
    private final MultiverseWorld world;
    private final MVDestination worldDestination;

    @Inject
    MultiverseLobbyTeleporter(@Assisted String id, MultiverseCore multiverse, LGConfiguration configuration)
            throws CannotCreateLobbyException {
        this.multiverse = multiverse;

        this.world = createClonedWorld(id, configuration);
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
        MultiverseWorld spawnWorld = multiverse.getMVWorldManager().getSpawnWorld();

        MVDestination destination = multiverse.getDestFactory().getDestination(spawnWorld.getName());

        multiverse.getSafeTTeleporter().teleport(player, player, destination);
    }

    @Override
    public void close() {
        multiverse.getMVWorldManager().deleteWorld(world.getName());
    }

    private MultiverseWorld createClonedWorld(@Assisted String id, LGConfiguration configuration)
            throws CannotCreateLobbyException {
        MVWorldManager worldManager = multiverse.getMVWorldManager();

        String sourceWorld = configuration.getDefaultWorld().orElse("loups_garous");
        String newWorld = LGGameManager.WORLD_PREFIX + id;

        if (!worldManager.cloneWorld(sourceWorld, newWorld)) {
            throw new CannotCreateLobbyException("Could not clone sourceWorld \"" + sourceWorld + "\".");
        }

        MultiverseWorld world = worldManager.getMVWorld(newWorld);
        world.setAlias(world.getName());

        return world;
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
