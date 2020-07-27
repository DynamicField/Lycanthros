package com.github.jeuxjeux20.loupsgarous.stages.dusk;

import com.github.jeuxjeux20.loupsgarous.stages.StagesModule;
import com.google.inject.AbstractModule;

public final class LGStagesDuskModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new StageModule());
        install(new DuskActions());
    }

    private static final class StageModule extends StagesModule {
        @Override
        protected void configureStages() {
            addStage(DuskStage.class);
        }
    }

    private static final class DuskActions extends DuskActionsModule {
        @Override
        protected void configureDuskActions() {
            addDuskAction(VoyanteDuskAction.class);
        }
    }
}
