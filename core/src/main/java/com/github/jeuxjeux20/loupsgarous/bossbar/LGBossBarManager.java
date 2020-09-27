package com.github.jeuxjeux20.loupsgarous.bossbar;

import com.github.jeuxjeux20.loupsgarous.event.CountdownTickEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseStartedEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.PhaseEventUtils;
import com.github.jeuxjeux20.loupsgarous.phases.TimedPhase;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhaseDescriptor;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import javax.annotation.Nonnull;

public class LGBossBarManager extends OrchestratorComponent {
    private final BossBar bossBar;

    @Inject
    LGBossBarManager(LGGameOrchestrator orchestrator) {
        super(orchestrator);
        this.bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);

        bind(bossBar::removeAll);
        bindModule(new UpdateModule());
    }

    public void update() {
        LGPhase phase = orchestrator.phases().current();
        LGPhaseDescriptor descriptor = orchestrator.phases().descriptors().get(phase.getClass());

        if (phase.isLogic()) return;

        if (descriptor.getName() == null) {
            bossBar.setVisible(false);
            return;
        }

        bossBar.removeAll();
        orchestrator.getAllMinecraftPlayers().forEach(bossBar::addPlayer);

        bossBar.setVisible(true);
        bossBar.setTitle(descriptor.getName());
        bossBar.setColor(descriptor.getColor().toBarColor(BarColor.GREEN));

        if (phase instanceof TimedPhase) {
            TimedPhase timedPhase = (TimedPhase) phase;
            bossBar.setProgress(1 / (timedPhase.getTotalSeconds() / (double) timedPhase.getSecondsLeft()));
        } else {
            bossBar.setProgress(1);
        }
    }

    private final class UpdateModule implements TerminableModule {
        @Override
        public void setup(@Nonnull TerminableConsumer consumer) {
            Events.subscribe(CountdownTickEvent.class)
                    .filter(e -> PhaseEventUtils.isCurrentPhaseCountdownEvent(orchestrator, e))
                    .handler(e -> update())
                    .bindWith(consumer);

            Events.merge(LGEvent.class, LGPhaseStartedEvent.class, LGPlayerQuitEvent.class)
                    .filter(orchestrator::isMyEvent)
                    .handler(e -> update())
                    .bindWith(consumer);
        }
    }
}
