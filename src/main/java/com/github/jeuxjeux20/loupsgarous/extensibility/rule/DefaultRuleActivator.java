package com.github.jeuxjeux20.loupsgarous.extensibility.rule;

import org.bukkit.plugin.Plugin;

public class DefaultRuleActivator implements RuleActivator {
    private final EventListenerRuleActivator eventListenerActivator;

    public DefaultRuleActivator(Plugin plugin) {
        this.eventListenerActivator = new EventListenerRuleActivator(plugin);
    }

    @Override
    public void activate(Rule rule) {
        eventListenerActivator.activate(rule);
    }

    @Override
    public void deactivate(Rule rule) {
        eventListenerActivator.deactivate(rule);
    }
}
