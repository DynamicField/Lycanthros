package com.github.df.loupsgarous.bossbar;

import com.github.df.loupsgarous.event.LGEvent;
import com.github.df.loupsgarous.event.phase.LGPhaseStartedEvent;
import com.github.df.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.OrchestratorComponent;
import com.github.df.loupsgarous.phases.CountdownTimedPhase;
import com.github.df.loupsgarous.phases.Phase;
import com.github.df.loupsgarous.phases.TimedPhase;
import io.reactivex.rxjava3.disposables.Disposable;
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

    public LGBossBarManager(LGGameOrchestrator orchestrator) {
        super(orchestrator);
        this.bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);

        bind(bossBar::removeAll);
    }

    @Override
    protected void onStart() {
        bindModule(new UpdateModule());
    }

    public void update() {
        Phase phase = orchestrator.phases().current();

        if (phase.isLogic()) return;

        if (phase.getDescriptor().getName() == null) {
            bossBar.setVisible(false);
            return;
        }

        bossBar.removeAll();
        orchestrator.getAllMinecraftPlayers().forEach(bossBar::addPlayer);

        bossBar.setVisible(true);
        bossBar.setTitle(phase.getDescriptor().getName());
        bossBar.setColor(phase.getDescriptor().getColor().toBarColor().orElse(BarColor.GREEN));

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
            Disposable subscription = orchestrator.phases().currentUpdates()
                    .compose(CountdownTimedPhase::notifyOnTick)
                    .subscribe(x -> update());

            consumer.bind(subscription::dispose);

            Events.merge(LGEvent.class, LGPhaseStartedEvent.class, LGPlayerQuitEvent.class)
                    .filter(orchestrator::isMyEvent)
                    .handler(e -> update())
                    .bindWith(consumer);
        }
    }
}
