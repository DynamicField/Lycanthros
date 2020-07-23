package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class CardsModule extends AbstractModule {
    private @Nullable Multibinder<LGCard> cardsBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureCards();
    }

    protected void configureBindings() {
    }

    protected void configureCards() {
    }

    private void actualConfigureCards() {
        cardsBinder = Multibinder.newSetBinder(binder(), LGCard.class);

        configureCards();
    }

    protected final void addCard(LGCard card) {
        Preconditions.checkState(cardsBinder != null, "addCard can only be used inside configureCards()");

        cardsBinder.addBinding().toInstance(card);
    }
}
