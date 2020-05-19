package com.github.jeuxjeux20.loupsgarous.game.cards.composition;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.VillageoisCard;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

public final class MutableComposition implements Composition {
    private final List<LGCard> cards;
    private int playerCount;

    public MutableComposition(int playerCount, List<LGCard> cards) {
        this.playerCount = playerCount;
        this.cards = Lists.newArrayList(cards);
        adaptCompositionSize();
    }

    @Override
    public ImmutableList<LGCard> getCards() {
        return ImmutableList.copyOf(cards);
    }

    @Override
    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        Preconditions.checkState(playerCount >= 1, "The player count must be positive.");

        this.playerCount = playerCount;
        adaptCompositionSize();
    }

    public void addPlayer() {
        playerCount++;
        adaptCompositionSize();
    }

    public boolean canRemove() {
        return playerCount > 1;
    }

    public void removePlayer() {
        Preconditions.checkState(canRemove(), "Minimum player count reached.");

        playerCount--;
        adaptCompositionSize();
    }

    public void addCard(LGCard card) {
        Preconditions.checkArgument(!cards.contains(card),
                "The composition already contains the card \"" + card + "\".");

        cards.add(card);
        adaptPlayerSize();
    }

    public boolean removeCardOfClass(Class<? extends LGCard> cardClass) {
        Optional<LGCard> maybeCard = getCards().stream().filter(x -> x.getClass() == cardClass).findAny();
        return maybeCard.map(this::removeCard).orElse(false);
    }

    public boolean removeCard(LGCard card) {
        if (!canRemove()) return false;

        try {
            return cards.remove(card);
        } finally {
            adaptPlayerSize();
        }
    }

    private void adaptPlayerSize() {
        if (cards.size() != playerCount) {
            playerCount = cards.size();
        }
    }

    private void adaptCompositionSize() {
        while (cards.size() != playerCount) {
            if (cards.size() > playerCount) {
                cards.remove(cards.size() - 1);
            } else {
                cards.add(new VillageoisCard());
            }
        }
    }
}
