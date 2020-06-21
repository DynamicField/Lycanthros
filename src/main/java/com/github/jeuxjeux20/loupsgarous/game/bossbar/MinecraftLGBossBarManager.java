package com.github.jeuxjeux20.loupsgarous.game.bossbar;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.CountdownTickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageStartedEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.StageEventUtils;
import com.github.jeuxjeux20.loupsgarous.game.stages.TimedStage;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import javax.annotation.Nonnull;

public class MinecraftLGBossBarManager implements LGBossBarManager {
    private final LGGameOrchestrator orchestrator;

    private final BossBar bossBar;

    @Inject
    MinecraftLGBossBarManager(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;

        this.bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
    }

    @Override
    public void update() {
        LGStage stage = orchestrator.stages().current();

        if (stage.isLogic()) return;

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

    @Override
    public TerminableModule createUpdateModule() {
        return new UpdateModule();
    }

    @Override
    public void close() {
        bossBar.removeAll();
    }

    private final class UpdateModule implements TerminableModule {
        @Override
        public void setup(@Nonnull TerminableConsumer consumer) {
            Events.subscribe(CountdownTickEvent.class)
                    .filter(e -> StageEventUtils.isCurrentStageCountdownEvent(orchestrator, e))
                    .handler(e -> update())
                    .bindWith(consumer);

            Events.subscribe(LGStageStartedEvent.class)
                    .filter(orchestrator::isMyEvent)
                    .handler(e -> update())
                    .bindWith(consumer);

            consumer.bind(MinecraftLGBossBarManager.this);
        }
    }
}
