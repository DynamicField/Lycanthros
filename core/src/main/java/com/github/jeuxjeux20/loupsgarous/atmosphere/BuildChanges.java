package com.github.jeuxjeux20.loupsgarous.atmosphere;

import com.google.common.collect.ImmutableSet;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Represents the changes that occurred due to a building action.
 * It's like WorldEdit's undo but simplified.
 */
public final class BuildChanges {
    public static final BuildChanges EMPTY = new BuildChanges(Collections.emptySet(), Collections.emptyList());

    private final ImmutableSet<BlockState> oldBlocks;
    private final ImmutableSet<Entity> newEntities;

    public BuildChanges(Set<BlockState> oldBlocks, Collection<Entity> newEntities) {
        this.oldBlocks = ImmutableSet.copyOf(oldBlocks);
        this.newEntities = ImmutableSet.copyOf(newEntities);
    }

    public static Builder builder() {
        return new Builder();
    }

    public ImmutableSet<BlockState> getOldBlocks() {
        return oldBlocks;
    }

    public ImmutableSet<Entity> getNewEntities() {
        return newEntities;
    }

    public void restore() {
        for (BlockState oldBlock : oldBlocks) {
            Block block = oldBlock.getWorld().getBlockAt(oldBlock.getLocation());
            block.setType(oldBlock.getType());
            block.setBlockData(oldBlock.getBlockData());
        }
        for (Entity newEntity : newEntities) {
            newEntity.remove();
        }
    }

    public static final class Builder {
        private final ImmutableSet.Builder<BlockState> oldBlocks = ImmutableSet.builder();
        private final ImmutableSet.Builder<Entity> newEntities = ImmutableSet.builder();

        public Builder takeBlockSnapshot(Block block) {
            oldBlocks.add(block.getState());
            return this;
        }

        public Builder addEntity(Entity entity) {
            newEntities.add(entity);
            return this;
        }

        public BuildChanges build() {
            return new BuildChanges(oldBlocks.build(), newEntities.build());
        }
    }
}
