package com.github.jeuxjeux20.loupsgarous.game.cards.composition;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import java.util.function.Consumer;

public final class ImmutableComposition implements Composition {
    private final ImmutableMultiset<LGCard> cards;

    public ImmutableComposition(Multiset<LGCard> cards) {
        this.cards = ImmutableMultiset.copyOf(cards);
    }

    public ImmutableComposition(Composition composition) {
        cards = composition.getContents();
    }

    public ImmutableComposition with(Consumer<Multiset<LGCard>> cardsMutator) {
        HashMultiset<LGCard> newCards = HashMultiset.create(cards);
        cardsMutator.accept(newCards);

        return new ImmutableComposition(newCards);
    }

    @Override
    public ImmutableMultiset<LGCard> getContents() {
        return cards;
    }
}
