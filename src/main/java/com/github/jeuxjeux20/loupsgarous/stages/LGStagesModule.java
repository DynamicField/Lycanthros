package com.github.jeuxjeux20.loupsgarous.stages;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;
import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.github.jeuxjeux20.loupsgarous.stages.descriptor.LGStagesDescriptorModule;
import com.github.jeuxjeux20.loupsgarous.stages.dusk.LGStagesDuskModule;
import com.github.jeuxjeux20.loupsgarous.stages.listeners.LGStagesListenersModule;
import com.github.jeuxjeux20.loupsgarous.stages.overrides.LGStageOverridesModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGStagesModule extends StagesModule {
    @Override
    protected void configureBindings() {
        bind(LGStagesOrchestrator.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.STAGES, LGStagesOrchestrator.class);
            }
        });

        install(new LGStageOverridesModule());
        install(new LGStagesListenersModule());
        install(new LGStagesDescriptorModule());

        install(new ListenersModule() {
            @Override
            protected void configureListeners() {
                addListener(GameStartStage.ResetTimerListener.class);
            }
        });
    }

    @Override
    protected void configureStages() {
        addStage(CupidonCoupleStage.class);
        install(new LGStagesDuskModule());
        addStage(LoupGarouVoteStage.class);
        addStage(SorcierePotionStage.class);
        addStage(NextTimeOfDayStage.class);
        addStage(RevealAllKillsStage.class);
        addStage(MaireElectionStage.class);
        addStage(VillageVoteStage.class);

        registerStageFactory(GameEndStage.class);
        registerStageFactory(GameStartStage.class);

        install(new FactoryModuleBuilder().build(ChasseurKillStage.Factory.class));
    }
}
