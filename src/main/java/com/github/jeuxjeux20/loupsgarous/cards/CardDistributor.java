package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.math.RandomUtils;

import java.util.*;

public class CardDistributor {
    public void distribute(Composition composition, Collection<? extends LGPlayer> players) {
        List<LGCard> cards = new ArrayList<>(composition.getContents());

        Preconditions.checkArgument(players.size() == cards.size(),
                "There isn't as much players as cards.");
        for (LGPlayer player : players) {
            LGCard card = getRandomCardAndRemove(cards);
            player.setCard(card);
        }
    }

    private LGCard getRandomCardAndRemove(List<LGCard> cards) {
        int index = RandomUtils.nextInt(cards.size());
        LGCard randomCard = cards.get(index);
        cards.remove(index);
        return randomCard;
    }
}
