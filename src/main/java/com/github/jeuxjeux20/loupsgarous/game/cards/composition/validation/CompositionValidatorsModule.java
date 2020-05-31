package com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class CompositionValidatorsModule extends AbstractModule {
    private @Nullable Multibinder<CompositionValidator> compositionValidatorBinder;

    @Override
    protected final void configure() {
        bind(CompositionValidator.class).to(CompositionValidatorAggregator.class);

        configureBindings();
        actualConfigureCompositionValidators();
    }

    protected void configureBindings() {
    }

    protected void configureCompositionValidators() {
    }

    private void actualConfigureCompositionValidators() {
        compositionValidatorBinder = Multibinder.newSetBinder(binder(), CompositionValidator.class);

        configureCompositionValidators();
    }

    protected final void addCompositionValidator(Class<? extends CompositionValidator> compositionValidator) {
        addCompositionValidator(TypeLiteral.get(compositionValidator));
    }

    protected final void addCompositionValidator(TypeLiteral<? extends CompositionValidator> compositionValidator) {
        Preconditions.checkState(compositionValidatorBinder != null,
                "addCompositionValidator can only be used inside configureCompositionValidators()");

        compositionValidatorBinder.addBinding().to(compositionValidator);
    }
}
