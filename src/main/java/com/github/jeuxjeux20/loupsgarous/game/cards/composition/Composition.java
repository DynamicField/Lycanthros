package com.github.jeuxjeux20.loupsgarous.game.cards.composition;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGLobby;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A set of cards, mainly used in a {@link LGLobby} to determine the cards to use in a game.
 */
public interface Composition {
    default ImmutableMap<Class<? extends LGCard>, List<LGCard>> getCardGroups() {
        return getCards().stream().collect(
                Collectors.collectingAndThen(Collectors.groupingBy(LGCard::getClass), ImmutableMap::copyOf));
    }

    ImmutableSet<LGCard> getCards();

    default int getPlayerCount() {
        return getCards().size();
    }
}
