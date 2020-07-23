package com.github.jeuxjeux20.loupsgarous.game.cards.composition;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGLobby;
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
