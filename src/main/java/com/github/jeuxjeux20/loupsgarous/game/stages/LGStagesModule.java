package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.stages.dusk.LGStagesDuskModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGStagesModule extends StagesModule {
    @Override
    protected void configureBindings() {
        install(new FactoryModuleBuilder()
                .implement(LGStagesOrchestrator.class, MinecraftLGStagesOrchestrator.class)
                .build(LGStagesOrchestrator.Factory.class));
    }

    @Override
    protected void configureStages() {
        addStage(CupidonCoupleStage.class);
        install(new LGStagesDuskModule());
        addStage(LoupGarouNightKillVoteStage.class);
        addStage(SorcierePotionStage.class);
        addStage(NextTimeOfDayStage.class);
        addStage(RevealAllKillsStage.class);
        addStage(VillageVoteStage.class);

        install(new FactoryModuleBuilder().build(ChasseurKillStage.Factory.class));
        install(new FactoryModuleBuilder().build(new TypeLiteral<AsyncLGGameStage.Factory<GameStartStage>>(){}));
        install(new FactoryModuleBuilder().build(new TypeLiteral<AsyncLGGameStage.Factory<GameEndStage>>(){}));
    }
}
