package com.github.jeuxjeux20.loupsgarous.extensibility.rule;

import com.github.jeuxjeux20.loupsgarous.event.GameEventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EventListenerRuleActivator implements RuleActivator {
    private final GameEventHandler handler;

    public EventListenerRuleActivator(Plugin plugin) {
        this.handler = new GameEventHandler(plugin);
    }

    @Override
    public void activate(Rule rule) {
        if (rule instanceof Listener) {
            handler.register((Listener) rule);
        }
    }

    @Override
    public void deactivate(Rule rule) {
        if (rule instanceof Listener) {
            handler.unregister((Listener) rule);
        }
    }
}
