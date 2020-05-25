package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.util.CollectorUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MutableLGGame implements LGGame {
    private static final Random random = new Random();

    private final Map<UUID, MutableLGPlayer> playersByUUID;
    private final MutableLGGameTurn turn = new MutableLGGameTurn();

    public MutableLGGame(Set<Player> players) {
        playersByUUID = players.stream().map(MutableLGPlayer::new)
                .collect(Collectors.toMap(MutableLGPlayer::getPlayerUUID, Function.identity(),
                        CollectorUtils::throwDuplicate, HashMap::new));
    }

    private static LGCard getRandomCardAndRemove(List<LGCard> cards) {
        int index = random.nextInt(cards.size());
        LGCard randomCard = cards.get(index);
        cards.remove(index);
        return randomCard;
    }

    public void distributeCards(Composition composition) {
        ImmutableList<LGCard> cards = composition.getCards();

        Preconditions.checkArgument(playersByUUID.size() == cards.size(),
                "There isn't as much players as cards.");

        ArrayList<LGCard> cardsToDistribute = new ArrayList<>(cards);

        for (MutableLGPlayer player : playersByUUID.values()) {
            LGCard card = getRandomCardAndRemove(cardsToDistribute);
            player.setCard(card);
        }
    }

    @Override
    public ImmutableSet<LGPlayer> getPlayers() {
        return ImmutableSet.copyOf(playersByUUID.values());
    }

    @Override
    public ImmutableMap<UUID, LGPlayer> getPlayerByUUIDMap() {
        return ImmutableMap.copyOf(playersByUUID);
    }

    @Override
    public Optional<LGPlayer> getPlayerByUUID(UUID playerUUID) {
        return Optional.ofNullable(playersByUUID.get(playerUUID));
    }

    public @Nullable MutableLGPlayer addPlayerIfAbsent(MutableLGPlayer player) {
        return playersByUUID.putIfAbsent(player.getPlayerUUID(), player);
    }

    public @Nullable MutableLGPlayer removePlayer(MutableLGPlayer player) {
        return removePlayer(player.getPlayerUUID());
    }

    public @Nullable MutableLGPlayer removePlayer(UUID playerUUID) {
        return playersByUUID.remove(playerUUID);
    }

    @Override
    public MutableLGGameTurn getTurn() {
        return turn;
    }
}