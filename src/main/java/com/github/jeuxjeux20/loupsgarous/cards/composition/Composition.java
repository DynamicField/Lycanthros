package com.github.jeuxjeux20.loupsgarous.cards.composition;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.google.common.collect.ImmutableMultiset;

/**
 * A set of cards.
 */
public interface Composition {
    ImmutableMultiset<LGCard> getContents();

    default int getPlayerCount() {
        return getContents().size();
    }
}
