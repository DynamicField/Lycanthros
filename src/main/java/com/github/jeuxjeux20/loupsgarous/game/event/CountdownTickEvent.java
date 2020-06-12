package com.github.jeuxjeux20.loupsgarous.game.event;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CountdownTickEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final Countdown countdown;

    public CountdownTickEvent(Countdown countdown) {
        this.countdown = countdown;
    }

    public Countdown getCountdown() {
        return countdown;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
