package com.github.jeuxjeux20.loupsgarous.game.interaction.finders;

public final class LGInteractionFindersModule extends InteractableFindersModule {
    @Override
    protected void configureBindings() {
        bind(InteractableFinder.class).to(InteractableFinderAggregator.class);
    }

    @Override
    protected void configureInteractableFinders() {
        addInteractableFinder(CurrentStageInteractableFinder.class);
    }
}
