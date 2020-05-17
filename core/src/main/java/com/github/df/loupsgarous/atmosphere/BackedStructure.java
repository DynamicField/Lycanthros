package com.github.df.loupsgarous.atmosphere;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class BackedStructure implements Structure {
    public static final BackedStructure EMPTY = new BackedStructure(Collections.emptyMap(), Collections.emptySet());

    private final ImmutableMap<Location, BlockTransformer> blockTransformers;
    private final ImmutableSet<EntitySpawner> entitySpawners;

    private BuildChanges buildChanges = BuildChanges.EMPTY;

    public BackedStructure(Map<Location, BlockTransformer> blockTransformers,
                           Collection<EntitySpawner> entitySpawners) {
        this.blockTransformers = ImmutableMap.copyOf(blockTransformers);
        this.entitySpawners = ImmutableSet.copyOf(entitySpawners);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void build() {
        BuildChanges.Builder changesBuilder = BuildChanges.builder();

        blockTransformers.forEach((location, transformer) -> {
            Block block = location.getBlock();

            changesBuilder.takeBlockSnapshot(block);
            transformer.transform(block);
        });

        for (EntitySpawner entitySpawner : entitySpawners) {
            Entity entity = entitySpawner.spawn();

            changesBuilder.addEntity(entity);
        }

        buildChanges = changesBuilder.build();
    }

    @Override
    public void remove() {
        buildChanges.restore();
        buildChanges = BuildChanges.EMPTY;
    }

    public static final class Builder {
        private final ImmutableMap.Builder<Location, BlockTransformer> blockTransformers = ImmutableMap.builder();
        private final ImmutableSet.Builder<EntitySpawner> entitySpawners = ImmutableSet.builder();

        public Builder transformBlock(Location location, BlockTransformer blockTransformer) {
            blockTransformers.put(location.clone(), blockTransformer);
            return this;
        }

        public Builder spawnEntity(EntitySpawner entitySpawner) {
            entitySpawners.add(entitySpawner);
            return this;
        }

        public BackedStructure build() {
            return new BackedStructure(blockTransformers.build(), entitySpawners.build());
        }
    }
}
