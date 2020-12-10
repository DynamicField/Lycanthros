package com.github.jeuxjeux20.loupsgarous.extensibility.registry;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public abstract class GameRegistryKey<T> {
    private final String name;

    protected GameRegistryKey(String name) {
        this.name = name;
    }

    public static <T> GameRegistryKey<T> createOrdered(String name) {
        return new GameRegistryKey<T>(name) {
            @Override
            public Registry<T> createRegistry(LGGameOrchestrator orchestrator) {
                return new OrderedRegistry<>();
            }
        };
    }

    public String getName() {
        return name;
    }

    public abstract Registry<T> createRegistry(LGGameOrchestrator orchestrator);

    public final Registry<T> get(LGGameOrchestrator orchestrator) {
        return orchestrator.getGameRegistry(this);
    }
}
