package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.stages.dusk.DuskStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.dusk.LGDuskActionsModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGStagesModule extends StagesModule {
    @Override
    protected void configureBindings() {
        install(new LGDuskActionsModule());
    }

    @Override
    protected void configureStages() {
        addStage(CupidonCoupleStage.class);
        addStage(DuskStage.class);
        addStage(LoupGarouNightKillVoteStage.class);
        addStage(SorcierePotionStage.class);
        addStage(NextTimeOfDayStage.class);
        addStage(RevealAllKillsStage.class);
        addStage(VillageVoteStage.class);

        install(new FactoryModuleBuilder().build(ChasseurKillStage.Factory.class));
    }
}
