package com.github.jeuxjeux20.loupsgarous.game.interaction.finders;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class InteractableFindersModule extends AbstractModule {
    private @Nullable Multibinder<InteractableFinder> interactableFinderBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureInteractableFinders();
    }

    protected void configureBindings() {
    }

    protected void configureInteractableFinders() {
    }

    private void actualConfigureInteractableFinders() {
        interactableFinderBinder = Multibinder.newSetBinder(binder(), InteractableFinder.class);

        configureInteractableFinders();
    }

    protected final void addInteractableFinder(Class<? extends InteractableFinder> interactableFinder) {
        addInteractableFinder(TypeLiteral.get(interactableFinder));
    }

    protected final void addInteractableFinder(TypeLiteral<? extends InteractableFinder> interactableFinder) {
        Preconditions.checkState(interactableFinderBinder != null, "addInteractableFinder can only be used inside configureInteractableFinders()");

        interactableFinderBinder.addBinding().to(interactableFinder);
    }
}
