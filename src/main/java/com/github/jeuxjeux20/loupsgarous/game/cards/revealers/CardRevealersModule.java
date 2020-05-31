package com.github.jeuxjeux20.loupsgarous.game.cards.revealers;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class CardRevealersModule extends AbstractModule {
    private @Nullable Multibinder<CardRevealer> cardRevealerBinder;

    @Override
    protected final void configure() {
        bind(CardRevealer.class).to(CardRevealerAggregator.class);

        configureBindings();
        actualConfigureCardRevealers();
    }

    protected void configureBindings() {
    }

    protected void configureCardRevealers() {
    }

    private void actualConfigureCardRevealers() {
        cardRevealerBinder = Multibinder.newSetBinder(binder(), CardRevealer.class);

        configureCardRevealers();
    }

    protected final void addCardRevealer(Class<? extends CardRevealer> cardRevealer) {
        addCardRevealer(TypeLiteral.get(cardRevealer));
    }

    protected final void addCardRevealer(TypeLiteral<? extends CardRevealer> cardRevealer) {
        Preconditions.checkState(cardRevealerBinder != null, "addCardRevealer can only be used inside configureCardRevealers()");

        cardRevealerBinder.addBinding().to(cardRevealer);
    }
}
