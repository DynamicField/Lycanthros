package com.github.jeuxjeux20.loupsgarous.game.cards.distribution;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import java.util.*;

public class RandomCardDistributor implements CardDistributor {
    private final Random random;

    @Inject
    RandomCardDistributor(Random random) {
        this.random = random;
    }

    @Override
    public Map<LGPlayer, LGCard> distribute(Composition composition, Set<LGPlayer> players) {
        List<LGCard> cards = new ArrayList<>(composition.getCards());

        Preconditions.checkArgument(players.size() == cards.size(),
                "There isn't as much players as cards.");

        Map<LGPlayer, LGCard> playerCards = new HashMap<>(cards.size());

        for (LGPlayer player : players) {
            LGCard card = getRandomCardAndRemove(cards);
            playerCards.put(player, card);
        }

        return playerCards;
    }

    private LGCard getRandomCardAndRemove(List<LGCard> cards) {
        int index = random.nextInt(cards.size());
        LGCard randomCard = cards.get(index);
        cards.remove(index);
        return randomCard;
    }
}
