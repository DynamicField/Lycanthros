package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

public abstract class ListenersModule extends AbstractModule {
    private @Nullable Multibinder<Listener> listenersBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureListeners();
    }

    protected void configureBindings() {
    }

    protected void configureListeners() {
    }

    private void actualConfigureListeners() {
        listenersBinder = Multibinder.newSetBinder(binder(), Listener.class);

        configureListeners();
    }

    protected final void addListener(Class<? extends Listener> listener) {
        addListener(TypeLiteral.get(listener));
    }

    protected final void addListener(TypeLiteral<? extends Listener> listener) {
        Preconditions.checkState(listenersBinder != null, "addListener can only be used inside configureListeners()");

        listenersBinder.addBinding().to(listener);
    }
}
