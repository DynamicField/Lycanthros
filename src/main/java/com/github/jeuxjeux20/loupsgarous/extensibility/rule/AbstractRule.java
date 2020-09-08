package com.github.jeuxjeux20.loupsgarous.extensibility.rule;

import com.github.jeuxjeux20.loupsgarous.extensibility.Extension;
import com.github.jeuxjeux20.loupsgarous.extensibility.ExtensionPoint;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRule implements Rule {
    protected final LGGameOrchestrator orchestrator;

    private final List<RuleListener> listeners = new ArrayList<>();
    private boolean enabled = false;

    private RuleActivator defaultActivator;

    public AbstractRule(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public final LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    protected RuleActivator getActivator() {
        return defaultActivator == null ?
                (defaultActivator = new DefaultRuleActivator(orchestrator.getPlugin())) :
                defaultActivator;
    }

    @Override
    public void enable() {
        if (enabled) {
            return;
        }

        enabled = true;
        getActivator().activate(this);
        for (RuleListener listener : listeners) {
            listener.onEnable();
        }
    }

    @Override
    public void disable() {
        if (!enabled) {
            return;
        }

        enabled = false;
        getActivator().deactivate(this);
        for (RuleListener listener : listeners) {
            listener.onDisable();
        }
    }

    @Override
    public void addListener(RuleListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(RuleListener listener) {
        listeners.remove(listener);
    }

    @SafeVarargs
    protected final <T> Extension<T> extend(ExtensionPoint<T> extensionPoint, T... contents) {
        return extensionPoint.extend(createExtensionName(extensionPoint), contents);
    }

    protected String createExtensionName(ExtensionPoint<?> extensionPoint) {
        return getClass().getSimpleName() + "_" + extensionPoint.getId();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("enabled", enabled)
                .toString();
    }
}
