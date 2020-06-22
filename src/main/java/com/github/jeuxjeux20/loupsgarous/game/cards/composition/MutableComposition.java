package com.github.jeuxjeux20.loupsgarous.game.cards.composition;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.VillageoisCard;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MutableComposition implements Composition {
    private final Set<LGCard> cards;
    private int playerCount;

    public MutableComposition(Composition composition) {
        this(composition.getPlayerCount(), composition.getCards());
    }

    public MutableComposition(int playerCount, Set<LGCard> cards) {
        this.playerCount = playerCount;
        this.cards = new HashSet<>(cards);
        adaptCompositionSize();
    }

    @Override
    public ImmutableSet<LGCard> getCards() {
        return ImmutableSet.copyOf(cards);
    }

    // Players

    @Override
    public final int getPlayerCount() {
        return playerCount;
    }

    public final void setPlayerCount(int playerCount) {
        Preconditions.checkArgument(isValidPlayerCount(playerCount), "The player count " + playerCount + " is invalid.");

        this.playerCount = playerCount;
        adaptCompositionSize();
        onChange();
    }

    public boolean isValidPlayerCount(int playerCount) {
        return playerCount >= 1;
    }

    // Cards

    public boolean canAddCard() {
        return isValidPlayerCount(playerCount + 1);
    }

    public boolean addCard(LGCard card) {
        if (!canAddCard()) {
            return false;
        }

        Preconditions.checkArgument(!cards.contains(card),
                "The composition already contains the card \"" + card + "\".");

        cards.add(card);
        adaptPlayerSize();
        onChange();

        return true;
    }

    public boolean canRemoveCard() {
        return isValidPlayerCount(playerCount - 1);
    }

    public boolean removeCard(LGCard card) {
        if (!canRemoveCard()) {
            return false;
        }

        boolean removed = cards.remove(card);

        adaptPlayerSize();
        onChange();

        return removed;
    }

    public final boolean removeCardOfClass(Class<? extends LGCard> cardClass) {
        Optional<LGCard> maybeCard = getCards().stream().filter(x -> x.getClass() == cardClass).findAny();

        return maybeCard.map(this::removeCard).orElse(false);
    }

    protected void onChange() {
    }

    // Adapt stuff

    private void adaptPlayerSize() {
        if (cards.size() != playerCount) {
            setPlayerCount(cards.size());
        }
    }

    private void adaptCompositionSize() {
        while (cards.size() != playerCount) {
            if (cards.size() > playerCount) {
                cards.remove(cards.iterator().next()); // Remove a random card.
            } else {
                cards.add(new VillageoisCard());
            }
        }
    }
}
