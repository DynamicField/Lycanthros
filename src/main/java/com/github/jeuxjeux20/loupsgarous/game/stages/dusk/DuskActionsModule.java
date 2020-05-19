package com.github.jeuxjeux20.loupsgarous.game.stages.dusk;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class DuskActionsModule extends AbstractModule {
    private @Nullable Multibinder<DuskStage.Action> duskActionBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureDuskActions();
    }

    protected void configureBindings() {
    }

    protected void configureDuskActions() {
    }

    private void actualConfigureDuskActions() {
        duskActionBinder = Multibinder.newSetBinder(binder(), DuskStage.Action.class);

        configureDuskActions();
    }

    protected final void addDuskAction(Class<? extends DuskStage.Action> duskAction) {
        addDuskAction(TypeLiteral.get(duskAction));
    }

    protected final void addDuskAction(TypeLiteral<? extends DuskStage.Action> duskAction) {
        Preconditions.checkState(duskActionBinder != null, "addDuskAction can only be used inside configureDuskActions()");

        duskActionBinder.addBinding().to(duskAction);
    }
}
