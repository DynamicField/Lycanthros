package com.github.jeuxjeux20.loupsgarous.phases.dusk;

import com.github.jeuxjeux20.loupsgarous.phases.PhasesModule;
import com.google.inject.AbstractModule;

public final class LGPhasesDuskModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new PhaseModule());
        install(new DuskActions());
    }

    private static final class PhaseModule extends PhasesModule {
        @Override
        protected void configurePhases() {
            addPhase(DuskPhase.class);
        }
    }

    private static final class DuskActions extends DuskActionsModule {
        @Override
        protected void configureDuskActions() {
            addDuskAction(VoyanteDuskAction.class);
        }
    }
}
