package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.events.stage.LGTimedStageTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public interface CountdownTimedStage extends TimedStage {
    Countdown getCountdown();

    @Override
    default int getSecondsLeft() {
        return getCountdown() == null ? 1 : getCountdown().getTimer();
    }

    @Override
    default int getTotalSeconds() {
        return getCountdown() == null ? 1 : getCountdown().getBiggestTimerValue();
    }

    default void addTickEvents(Countdown.Builder builder) {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        builder.tick(() ->
                pluginManager.callEvent(new LGTimedStageTickEvent(this.getOrchestrator(), this))
        );
    }

}
