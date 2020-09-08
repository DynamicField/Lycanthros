package com.github.jeuxjeux20.loupsgarous.event;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorDependent;
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
    public void register(Listener target) {
        for (Method method : target.getClass().getMethods()) {
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

    public void unregister(Listener target) {
        HandlerList.unregisterAll(target);
    }

    private void handle(Listener listener, Event event, Method method) {
        if (event instanceof LGEvent && listener instanceof OrchestratorDependent) {
            if (((LGEvent) event).getOrchestrator() !=
                ((OrchestratorDependent) listener).getOrchestrator()) {
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
