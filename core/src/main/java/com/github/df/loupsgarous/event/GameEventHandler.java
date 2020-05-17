package com.github.df.loupsgarous.event;

import com.github.df.loupsgarous.game.OrchestratorAware;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class GameEventHandler {
    private final Plugin plugin;

    public GameEventHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public <T extends Listener & OrchestratorAware> void register(T target) {
        plugin.getServer().getPluginManager().registerEvents(target, plugin);

        for (Method method : target.getClass().getDeclaredMethods()) {
            method.setAccessible(true);

            GameEvent annotation = method.getAnnotation(GameEvent.class);

            if (annotation != null) {
                if (method.getParameterCount() != 1 ||
                    !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    plugin.getLogger().warning(
                            "Method " + method + " is annotated with GameEvent but " +
                            "does not take only one Event parameter."
                    );
                    continue;
                }
                Class<? extends Event> eventType = (Class<? extends Event>) method.getParameterTypes()[0];
                plugin.getServer().getPluginManager().registerEvent(
                        eventType, target, annotation.priority(), (l, e) -> handle(l, e, method), plugin);

                plugin.getLogger().fine("Registered method : " + method);
            }
        }
    }

    public <T extends Listener & OrchestratorAware> void unregister(T target) {
        HandlerList.unregisterAll(target);
    }

    private void handle(Listener listener, Event event, Method method) {
        if (event instanceof LGEvent) {
            if (((LGEvent) event).getOrchestrator() !=
                ((OrchestratorAware) listener).getOrchestrator()) {
                return;
            }
        }

        try {
            method.invoke(listener, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().log(Level.WARNING,
                    "Failed to invoke method " + method + " while passing event " + event, e);
        }
    }
}
