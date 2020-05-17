package com.github.df.loupsgarous.extensibility;

import com.github.df.loupsgarous.event.GameEventHandler;
import com.github.df.loupsgarous.event.extensibility.ModDisabledEvent;
import com.github.df.loupsgarous.event.extensibility.ModEnabledEvent;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.OrchestratorAware;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class Mod implements TerminableConsumer, Listener, OrchestratorAware {
    protected final LGGameOrchestrator orchestrator;
    private CompositeTerminable terminableRegistry = CompositeTerminable.create();
    private final GameEventHandler eventHandler;
    private ModDescriptor descriptor;
    private boolean enabled;

    public Mod(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
        this.eventHandler = new GameEventHandler(orchestrator.getLoupsGarous());
    }

    public boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;

            if (enabled) {
                doActivate();
            } else {
                doDeactivate();
            }
        }
    }

    private void doActivate() {
        eventHandler.register(this);
        activate();
        Events.call(new ModEnabledEvent(orchestrator, this));
    }

    private void doDeactivate() {
        eventHandler.unregister(this);
        terminableRegistry.closeAndReportException();
        terminableRegistry = CompositeTerminable.create();
        deactivate();
        Events.call(new ModDisabledEvent(orchestrator, this));
    }

    protected void activate() {
    }

    protected void deactivate() {
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    @NotNull
    @Override
    public <T extends AutoCloseable> T bind(@NotNull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    public ModDescriptor getDescriptor() {
        if (descriptor == null) {
            descriptor = ModDescriptor.fromClass(getClass());
        }
        return descriptor;
    }

    @FunctionalInterface
    public interface Factory {
        Mod create(LGGameOrchestrator orchestrator);
    }
}
