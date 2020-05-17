package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.google.inject.Inject;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class WorldCleaner {
    private final MultiverseCore multiverse;
    private final LoupsGarous plugin;
    private final LGGameManager gameManager;

    @Inject
    public WorldCleaner(MultiverseCore multiverse, LoupsGarous plugin, LGGameManager gameManager) {
        this.multiverse = multiverse;
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public void clean() {
        MVWorldManager worldManager = multiverse.getMVWorldManager();
        worldManager.getMVWorlds().stream()
                .filter(world -> world.getName().startsWith(LGGameManager.WORLD_PREFIX))
                .filter(this::isNotUsed)
                .forEach(world -> {
                    plugin.getLogger().info("Deleting world " + world + "...");
                    if (!worldManager.deleteWorld(world.getName())) {
                        plugin.getLogger().warning("Could not delete world " + world + ".");
                    }
                });
    }

    private boolean isNotUsed(MultiverseWorld x) {
        return gameManager.getOngoingGames().stream().noneMatch(
                g -> x.getName().substring(LGGameManager.WORLD_PREFIX.length()).equals(g.getId().toString())
        );
    }
}
