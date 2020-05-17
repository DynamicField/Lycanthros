package com.github.jeuxjeux20.loupsgarous.game.composition;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.stream.Collectors;

public interface Composition {
    default ImmutableMap<Class<? extends LGCard>, List<LGCard>> getCardGroups() {
        return getCards().stream().collect(
                Collectors.collectingAndThen(Collectors.groupingBy(LGCard::getClass), ImmutableMap::copyOf));
    }

    ImmutableList<LGCard> getCards();

    int getPlayerCount();
}
