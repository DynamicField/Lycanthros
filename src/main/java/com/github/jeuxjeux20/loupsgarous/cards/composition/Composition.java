package com.github.jeuxjeux20.loupsgarous.cards.composition;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.lobby.LGLobby;
import com.google.common.collect.ImmutableMultiset;

/**
 * A set of cards, mainly used in a {@link LGLobby} to determine the cards to use in a game.
 */
public interface Composition {
    ImmutableMultiset<LGCard> getContents();

    default int getPlayerCount() {
        return getContents().size();
    }
}
