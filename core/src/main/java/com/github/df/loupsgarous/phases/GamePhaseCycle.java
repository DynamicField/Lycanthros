package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.event.registry.RegistryChangeEvent;
import com.github.df.loupsgarous.extensibility.ContentFactory;
import com.github.df.loupsgarous.extensibility.registry.GameRegistries;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.Events;
import me.lucko.helper.event.SingleSubscription;

public class GamePhaseCycle extends PhaseCycle {
    private SingleSubscription<RegistryChangeEvent>
            subscription;

    public GamePhaseCycle(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        updatePhases();
        subscription = Events.subscribe(RegistryChangeEvent.class)
                .filter(e -> e.getRegistry() == GameRegistries.PHASES.get(orchestrator))
                .handler(e -> updatePhases());
    }

    private void updatePhases() {
        ImmutableSet<ContentFactory<? extends RunnablePhase>> phases =
                GameRegistries.PHASES.get(orchestrator).getValues();

        setPhases(phases);
    }

    @Override
    protected void stopProgram() {
        super.stopProgram();
        subscription.unregister();
    }
}
