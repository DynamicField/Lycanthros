package com.github.jeuxjeux20.loupsgarous.game.cards.composition;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.VillageoisCard;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.github.jeuxjeux20.loupsgarous.util.ThrowingFunction;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class MutableComposition implements Composition {
    private final List<LGCard> cards;
    private int playerCount;

    public MutableComposition(Composition composition) {
        this(composition.getPlayerCount(), composition.getCards());
    }

    public MutableComposition(int playerCount) {
        this(playerCount, Collections.emptyList());
    }

    public MutableComposition(int playerCount, List<LGCard> cards) {
        this.playerCount = playerCount;
        this.cards = new ArrayList<>(cards);
        adaptCompositionSize();
    }

    @Override
    public ImmutableList<LGCard> getCards() {
        return ImmutableList.copyOf(cards);
    }

    // Players

    @Override
    public final int getPlayerCount() {
        return playerCount;
    }

    public final void setPlayerCount(int playerCount) throws IllegalPlayerCountException {
        checkPlayerCount(playerCount);

        this.playerCount = playerCount;
        adaptCompositionSize();
        onChange();
    }

    protected void checkPlayerCount(int playerCount) throws IllegalPlayerCountException {
        if (playerCount < 1) {
            throw new IllegalPlayerCountException("Nombre minimum de joueurs atteint.");
        }
    }

    public final void addPlayer() throws IllegalPlayerCountException {
        setPlayerCount(playerCount);
        adaptCompositionSize();
        onChange();
    }

    public final void removePlayer() throws IllegalPlayerCountException {
        setPlayerCount(playerCount - 1);
        adaptCompositionSize();
        onChange();
    }

    // Cards

    public void addCard(LGCard card) throws IllegalPlayerCountException {
        checkPlayerCount(playerCount + 1);

        Preconditions.checkArgument(!cards.contains(card),
                "The composition already contains the card \"" + card + "\".");

        cards.add(card);
        adaptPlayerSize();
        onChange();
    }

    public boolean removeCard(LGCard card) throws IllegalPlayerCountException {
        checkPlayerCount(playerCount - 1);

        boolean removed = cards.remove(card);

        adaptPlayerSize();
        onChange();

        return removed;
    }

    public final boolean removeCardOfClass(Class<? extends LGCard> cardClass) throws IllegalPlayerCountException {
        Optional<LGCard> maybeCard = getCards().stream().filter(x -> x.getClass() == cardClass).findAny();

        return OptionalUtils.mapThrows(maybeCard, (RemoveCardFunction) this::removeCard).orElse(false);
    }

    protected void onChange() {
    }

    // Adapt stuff

    private void adaptPlayerSize() throws IllegalPlayerCountException {
        if (cards.size() != playerCount) {
            setPlayerCount(cards.size());
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

    private interface RemoveCardFunction extends ThrowingFunction<LGCard, Boolean, IllegalPlayerCountException> {
    }
}
