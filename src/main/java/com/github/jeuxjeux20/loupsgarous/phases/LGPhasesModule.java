package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;
import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhasesDescriptorModule;
import com.github.jeuxjeux20.loupsgarous.phases.dusk.LGPhasesDuskModule;
import com.github.jeuxjeux20.loupsgarous.phases.listeners.LGPhasesListenersModule;
import com.github.jeuxjeux20.loupsgarous.phases.overrides.LGPhaseOverridesModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGPhasesModule extends PhasesModule {
    @Override
    protected void configureBindings() {
        bind(LGPhasesOrchestrator.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.PHASES, LGPhasesOrchestrator.class);
            }
        });

        install(new LGPhaseOverridesModule());
        install(new LGPhasesListenersModule());
        install(new LGPhasesDescriptorModule());

        install(new ListenersModule() {
            @Override
            protected void configureListeners() {
                addListener(GameStartPhase.ResetTimerListener.class);
            }
        });
    }

    @Override
    protected void configurePhases() {
        addPhase(CupidonCouplePhase.class);
        install(new LGPhasesDuskModule());
        addPhase(LoupGarouVotePhase.class);
        addPhase(SorcierePotionPhase.class);
        addPhase(NextTimeOfDayPhase.class);
        addPhase(RevealAllKillsPhase.class);
        addPhase(MaireElectionPhase.class);
        addPhase(VillageVotePhase.class);

        registerPhaseFactory(GameEndPhase.class);
        registerPhaseFactory(GameStartPhase.class);

        install(new FactoryModuleBuilder().build(ChasseurKillPhase.Factory.class));
    }
}
