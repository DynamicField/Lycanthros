package com.github.df.loupsgarous.atmosphere;

import org.bukkit.entity.Entity;

@FunctionalInterface
public interface EntitySpawner {
    Entity spawn();
}
