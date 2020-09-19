package com.github.jeuxjeux20.loupsgarous.extensibility;

import java.util.ArrayList;
import java.util.List;

public abstract class ExtensionGroup {
    private boolean enabled;
    private final Rule rule;

    public ExtensionGroup(Rule rule) {
        this.rule = rule;
    }

    public static List<Extension<?>> getAll(Iterable<? extends ExtensionGroup> groups) {
        List<Extension<?>> extensions = new ArrayList<>();

        for (ExtensionGroup group : groups) {
            extensions.addAll(group.getExtensions());
        }

        return extensions;
    }

    public abstract List<Extension<?>> getExtensions();

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        if (enabled) {
            return;
        }

        enabled = true;
        rule.refresh();
    }

    public void disable() {
        if (!enabled) {
            return;
        }

        enabled = false;
        rule.refresh();
    }
}
