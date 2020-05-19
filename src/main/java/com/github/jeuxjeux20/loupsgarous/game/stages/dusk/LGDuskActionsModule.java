package com.github.jeuxjeux20.loupsgarous.game.stages.dusk;

public final class LGDuskActionsModule extends DuskActionsModule {
    @Override
    protected void configureDuskActions() {
        addDuskAction(VoyanteDuskAction.class);
    }
}
