package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class WinConditionsModule extends AbstractModule {
    private @Nullable Multibinder<WinCondition> winConditionBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureWinConditions();
    }

    protected void configureBindings() {
    }

    protected void configureWinConditions() {
    }

    private void actualConfigureWinConditions() {
        winConditionBinder = Multibinder.newSetBinder(binder(), WinCondition.class);

        configureWinConditions();
    }

    protected final void addWinCondition(Class<? extends WinCondition> winCondition) {
        addWinCondition(TypeLiteral.get(winCondition));
    }

    protected final void addWinCondition(TypeLiteral<? extends WinCondition> winCondition) {
        Preconditions.checkState(winConditionBinder != null, "addWinCondition can only be used inside configureWinConditions()");

        winConditionBinder.addBinding().to(winCondition);
    }
}
