package com.github.jeuxjeux20.loupsgarous.phases.dusk;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class DuskActionsModule extends AbstractModule {
    private @Nullable Multibinder<DuskAction> duskActionBinder;

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
        duskActionBinder = Multibinder.newSetBinder(binder(), DuskAction.class);

        configureDuskActions();
    }

    protected final void addDuskAction(Class<? extends DuskAction> duskAction) {
        addDuskAction(TypeLiteral.get(duskAction));
    }

    protected final void addDuskAction(TypeLiteral<? extends DuskAction> duskAction) {
        Preconditions.checkState(duskActionBinder != null, "addDuskAction can only be used inside configureDuskActions()");

        duskActionBinder.addBinding().to(duskAction).in(OrchestratorScoped.class);
    }
}
