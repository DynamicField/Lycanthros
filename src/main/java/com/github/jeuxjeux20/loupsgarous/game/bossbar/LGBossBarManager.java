package com.github.jeuxjeux20.loupsgarous.game.bossbar;

import com.github.jeuxjeux20.loupsgarous.game.AbstractOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.event.CountdownTickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageStartedEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.StageEventUtils;
import com.github.jeuxjeux20.loupsgarous.game.stages.TimedStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.descriptor.LGStageDescriptor;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import javax.annotation.Nonnull;

@OrchestratorScoped
public class LGBossBarManager extends AbstractOrchestratorComponent {
    private final BossBar bossBar;

    @Inject
    LGBossBarManager(LGGameOrchestrator orchestrator) {
        super(orchestrator);
        this.bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);

        bind(bossBar::removeAll);
        bindModule(new UpdateModule());
    }

    public void update() {
        LGStage stage = orchestrator.stages().current();
        LGStageDescriptor descriptor = orchestrator.stages().descriptors().get(stage.getClass());

        if (stage.isLogic()) return;

        if (descriptor.getName() == null) {
            bossBar.setVisible(false);
            return;
        }

        bossBar.removeAll();
        orchestrator.getAllMinecraftPlayers().forEach(bossBar::addPlayer);

        bossBar.setVisible(true);
        bossBar.setTitle(descriptor.getName());
        bossBar.setColor(descriptor.getColor().toBarColor(BarColor.GREEN));

        if (stage instanceof TimedStage) {
            TimedStage timedStage = (TimedStage) stage;
            bossBar.setProgress(1 / (timedStage.getTotalSeconds() / (double) timedStage.getSecondsLeft()));
        } else {
            bossBar.setProgress(1);
        }
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
        }
    }
}
