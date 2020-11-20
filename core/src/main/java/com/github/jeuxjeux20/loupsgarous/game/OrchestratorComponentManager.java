package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.Registration;
import com.github.jeuxjeux20.loupsgarous.actionbar.LGActionBarManager;
import com.github.jeuxjeux20.loupsgarous.bossbar.LGBossBarManager;
import com.github.jeuxjeux20.loupsgarous.inventory.LGInventoryManager;
import com.github.jeuxjeux20.loupsgarous.scoreboard.LGScoreboardManager;
import com.google.common.base.Preconditions;
import me.lucko.helper.terminable.Terminable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class OrchestratorComponentManager {
    private final Map<Class<? extends OrchestratorComponent>, OrchestratorComponent> components =
            new HashMap<>();

    private final MinecraftLGGameOrchestrator orchestrator;

    OrchestratorComponentManager(MinecraftLGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;

        register(LGActionBarManager.class, new LGActionBarManager(orchestrator));
        register(LGBossBarManager.class, new LGBossBarManager(orchestrator));
        register(LGInventoryManager.class, new LGInventoryManager(orchestrator));
        register(LGScoreboardManager.class, new LGScoreboardManager(orchestrator));
    }

    public <T extends OrchestratorComponent> Terminable register(
            Class<T> type,
            Function<LGGameOrchestrator, ? extends T> factory) {
        return register(type, factory.apply(orchestrator));
    }

    public <T extends OrchestratorComponent> Terminable register(
            Class<T> type, T component) {
        Preconditions.checkArgument(!component.isClosed(), "The given component has been closed.");
        if (components.containsKey(type)) {
            throw new IllegalStateException("A component " + type + " is already present");
        }
        if (component.getOrchestrator() != orchestrator) {
            throw new IllegalArgumentException(
                    "The component " + component + " has been registered" +
                    " on the wrong orchestrator.");
        }

        MyRegistration registration = new MyRegistration(type);
        components.put(type, component);
        component.onStart();

        return registration;
    }

    @SuppressWarnings("unchecked")
    public <T extends OrchestratorComponent> Optional<T> get(Class<? extends T> type) {
        return Optional.ofNullable((T) components.get(type));
    }

    private void unregister(Class<? extends OrchestratorComponent> type) {
        OrchestratorComponent component = components.get(type);
        if (component == null) {
            throw new IllegalArgumentException("No component found of type " + type);
        }
        components.remove(type);
        component.onStop();
        component.closeAndReportException();
    }

    void close() {
        OrchestratorComponent[] toClose = components.values().toArray(new OrchestratorComponent[0]);

        for (OrchestratorComponent component : toClose) {
            if (!component.isClosed()) {
                component.onStop();
            }
        }

        components.clear();

        for (OrchestratorComponent component : toClose) {
            if (!component.isClosed()) {
                component.closeAndReportException();
            }
        }
    }

    private final class MyRegistration implements Registration {
        private final Class<? extends OrchestratorComponent> type;
        private boolean registered = true;

        MyRegistration(Class<? extends OrchestratorComponent> type) {
            this.type = type;
        }

        @Override
        public void unregister() {
            if (registered) {
                OrchestratorComponentManager.this.unregister(type);
                registered = false;
            }
        }

        @Override
        public boolean isRegistered() {
            return registered;
        }
    }
}
