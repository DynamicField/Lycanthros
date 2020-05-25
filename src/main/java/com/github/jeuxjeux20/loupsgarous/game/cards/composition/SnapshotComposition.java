package com.github.jeuxjeux20.loupsgarous.game.cards.composition;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;

public final class SnapshotComposition implements Composition {
    private final ImmutableList<LGCard> cards;
    private final ImmutableMap<Class<? extends LGCard>, List<LGCard>> cardGroups;

    public SnapshotComposition(Composition composition) {
        cards = composition.getCards();
        cardGroups = Composition.super.getCardGroups(); // Cache them.
    }

    @Override
    public ImmutableList<LGCard> getCards() {
        return cards;
    }

    @Override
    public ImmutableMap<Class<? extends LGCard>, List<LGCard>> getCardGroups() {
        return cardGroups;
    }
}
