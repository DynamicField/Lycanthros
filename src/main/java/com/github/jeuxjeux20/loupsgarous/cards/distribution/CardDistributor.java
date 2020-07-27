package com.github.jeuxjeux20.loupsgarous.cards.distribution;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;

import java.util.Map;
import java.util.Set;

/**
 * Distributes cards from a composition.
 */
public interface CardDistributor {
    Map<LGPlayer, LGCard> distribute(Composition composition, Set<LGPlayer> players);
}
