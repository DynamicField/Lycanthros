package com.github.df.loupsgarous.atmosphere;

import org.bukkit.block.Block;

@FunctionalInterface
public interface BlockTransformer {
    void transform(Block block);
}
