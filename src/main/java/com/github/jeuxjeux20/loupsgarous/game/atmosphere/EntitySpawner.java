package com.github.jeuxjeux20.loupsgarous.game.atmosphere;

import org.bukkit.entity.Entity;

@FunctionalInterface
public interface EntitySpawner {
    Entity spawn();
}
