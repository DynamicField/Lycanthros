package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.Plugin;
import com.github.jeuxjeux20.loupsgarous.config.annotations.DefaultWorld;
import com.github.jeuxjeux20.loupsgarous.config.annotations.Pool;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.event.MVWorldDeleteEvent;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

@Singleton
class MinecraftMultiverseWorldPool implements MultiverseWorldPool {
    private static final String WORLD_PREFIX = "lg_lobby_";

    private final MultiverseCore multiverse;
    private final String sourceWorld;
    private final int minWorlds;
    private final Provider<Integer> maxWorldsProvider;
    private final Logger logger;

    private final ConcurrentLinkedDeque<MultiverseWorld> availableWorlds = new ConcurrentLinkedDeque<>();
    private final CopyOnWriteArraySet<MultiverseWorld> allWorlds = new CopyOnWriteArraySet<>();

    @Inject
    MinecraftMultiverseWorldPool(MultiverseCore multiverse,
                                 @DefaultWorld String sourceWorld,
                                 @Pool.MinWorlds int minWorlds,
                                 @Pool.MaxWorlds Provider<Integer> maxWorldsProvider,
                                 @Plugin Logger logger) {
        this.multiverse = multiverse;
        this.sourceWorld = sourceWorld;
        this.minWorlds = minWorlds;
        this.maxWorldsProvider = maxWorldsProvider;
        this.logger = logger;

        try {
            initializeWorlds();
        } catch (WorldCreationException e) {
            logger.severe("Couldn't create the world while initializing the world pool: " + e.getMessage());
            logger.severe("The following error may prevent the plugin from being used.");
            logger.severe("Please check the default-world entry in config.yml and restart the server.");
        }

        Events.subscribe(MVWorldDeleteEvent.class)
                .handler(e -> removeWorld(e.getWorld()));
    }

    @Override
    public PooledMultiverseWorld get() throws WorldCreationException {
        MultiverseWorld availableWorld = availableWorlds.poll();

        if (availableWorld == null) {
            ensureWorldCapacity();

            logger.warning("All worlds from the pool have been used, creating a new one: THIS WILL CAUSE A HUGE AMOUNT OF LAG.");
            logger.warning("To fix this, either add more worlds to the pool (world-pool -> min-worlds).");
            logger.warning("Or restrict the maximum amount of games (world-pool -> max-worlds).");
            logger.warning("NOTE: If you have recently changed min-worlds, please restart the server for changes");
            logger.warning("to take effect.");

            availableWorld = createWorld();
            markAsUnavailable(availableWorld);
        }

        return new PooledWrapper(availableWorld);
    }

    @Override
    public synchronized boolean isInLobbyWorld(Player player) {
        MultiverseWorld multiverseWorld = multiverse.getMVWorldManager().getMVWorld(player.getWorld());

        return multiverseWorld != null && allWorlds.contains(multiverseWorld);
    }

    private synchronized MultiverseWorld createWorld() throws WorldCloneFailedException, MaximumWorldCountReachedException {
        ensureWorldCapacity();

        MVWorldManager worldManager = multiverse.getMVWorldManager();

        String newWorld = WORLD_PREFIX + UUID.randomUUID();

        if (!worldManager.cloneWorld(sourceWorld, newWorld)) {
            throw new WorldCloneFailedException("Could not clone sourceWorld \"" + sourceWorld + "\".", sourceWorld);
        }

        MultiverseWorld world = worldManager.getMVWorld(newWorld);
        world.setAlias(world.getName());

        return world;
    }

    private void ensureWorldCapacity() throws MaximumWorldCountReachedException {
        if (isMaximumReached()) {
            throw new MaximumWorldCountReachedException("There are too many worlds (max " + getMaxWorlds() + ").", getMaxWorlds());
        }
    }

    private void initializeWorlds() throws WorldCloneFailedException, MaximumWorldCountReachedException {
        MVWorldManager worldManager = multiverse.getMVWorldManager();

        for (MultiverseWorld world : worldManager.getMVWorlds()) {
            if (world.getName().startsWith(WORLD_PREFIX)) {
                if (!isMaximumReached()) {
                    logger.fine("Found pool world: " + world.getName());
                    markAsAvailable(world);
                } else {
                    logger.info("Deleting pool world because it is exceeding the maximum (" + getMaxWorlds() + ").");
                    worldManager.deleteWorld(world.getName());
                }
            }
        }

        if (availableWorlds.size() < minWorlds) {
            logger.info("Creating a world pool, this might take a while...");

            do {
                MultiverseWorld newWorld = createWorld();
                markAsAvailable(newWorld);
            } while (availableWorlds.size() < minWorlds);
        }
    }

    private boolean markAsAvailable(MultiverseWorld world) {
        allWorlds.add(world);

        logger.fine("Marking as available: " + world.getName());
        return availableWorlds.offerFirst(world);
    }

    private boolean markAsUnavailable(MultiverseWorld world) {
        allWorlds.add(world);

        logger.fine("Marking as unavailable: " + world.getName());
        return availableWorlds.remove(world);
    }

    private void removeWorld(MultiverseWorld world) {
        allWorlds.remove(world);
        availableWorlds.remove(world);
    }

    private boolean isMaximumReached() {
        return allWorlds.size() >= getMaxWorlds();
    }

    private int getMaxWorlds() {
        return maxWorldsProvider.get();
    }

    private class PooledWrapper implements PooledMultiverseWorld {
        private final MultiverseWorld world;

        public PooledWrapper(MultiverseWorld world) {
            this.world = world;
        }

        @Override
        public MultiverseWorld get() {
            return world;
        }

        @Override
        public void close() {
            if (!allWorlds.contains(world)) return; // Manually deleted using multiverse.
            if (!markAsAvailable(world)) {
                throw new IllegalStateException("This pooled world is already closed.");
            }
        }

        @Override
        public boolean isClosed() {
            return availableWorlds.contains(world) || !allWorlds.contains(world);
        }
    }
}
