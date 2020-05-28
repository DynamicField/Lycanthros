package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.stage.LGStageChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.stage.LGTimedStageTickEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGGameStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.TimedStage;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.HashMap;

public class UpdateBossBarListener implements Listener {
    private final HashMap<LGGameOrchestrator, BossBar> gameBossBar = new HashMap<>();

    @EventHandler
    public void onLGTimedStageTick(LGTimedStageTickEvent event) {
        updateBossBar(event.getStage(), event.getOrchestrator());
    }

    @EventHandler
    public void onLGStageChanged(LGStageChangeEvent event) {
        updateBossBar(event.getStage(), event.getOrchestrator());
    }

    @EventHandler
    public void onLGGameDeleted(LGGameDeletedEvent event) {
        removeBossBar(event);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (!(event.getPlugin() instanceof LoupsGarous)) return;
        gameBossBar.values().forEach(BossBar::removeAll);
        gameBossBar.clear();
    }

    private void removeBossBar(LGEvent event) {
        BossBar bossBar = gameBossBar.remove(event.getOrchestrator());
        if (bossBar == null) return;
        bossBar.removeAll();
    }

    private void updateBossBar(LGGameStage stage, LGGameOrchestrator orchestrator) {
        if (stage.isLogic()) return;

        BossBar bossBar = gameBossBar.computeIfAbsent(orchestrator,
                k -> Bukkit.createBossBar(stage.getName(), BarColor.GREEN, BarStyle.SOLID));

        if (stage.getName() == null) {
            bossBar.setVisible(false);
            return;
        }

        bossBar.removeAll();
        orchestrator.getAllMinecraftPlayers().forEach(bossBar::addPlayer);

        bossBar.setVisible(true);
        bossBar.setTitle(stage.getName());
        bossBar.setColor(stage.getBarColor());

        if (stage instanceof TimedStage) {
            TimedStage timedStage = (TimedStage) stage;
            bossBar.setProgress(1 / (timedStage.getTotalSeconds() / (double) timedStage.getSecondsLeft()));
        } else {
            bossBar.setProgress(1);
        }
    }
}
