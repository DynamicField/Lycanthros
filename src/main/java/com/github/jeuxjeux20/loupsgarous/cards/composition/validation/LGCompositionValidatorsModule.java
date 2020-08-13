package com.github.jeuxjeux20.loupsgarous.cards.composition.validation;

public final class LGCompositionValidatorsModule extends CompositionValidatorsModule {
    @Override
    protected void configureBindings() {

    }

    @Override
    protected void configureCompositionValidators() {
        addCompositionValidator(PossibleCouplesCupidonCompositionValidator.class);
        addCompositionValidator(UniqueCardCompositionValidator.class);
        addCompositionValidator(MultipleTeamsCompositionValidator.class);
    }
}
