package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.chat.AnonymizedChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MutableLGGame implements LGGame {
    private static final Random random = new Random();

    private final String id;
    private final Map<UUID, MutableLGPlayer> playersByUUID = new HashMap<>();
    private final MutableLGGameTurn turn = new MutableLGGameTurn();
    private final Map<AnonymizedChatChannel, List<String>> anonymizedNames = new HashMap<>();
    private @Nullable LGEnding ending;

    public MutableLGGame(String id) {
        this.id = id;
    }

    private static LGCard getRandomCardAndRemove(List<LGCard> cards) {
        int index = random.nextInt(cards.size());
        LGCard randomCard = cards.get(index);
        cards.remove(index);
        return randomCard;
    }

    public void distributeCards(Composition composition) {
        ImmutableSet<LGCard> cards = composition.getCards();

        Preconditions.checkArgument(playersByUUID.size() == cards.size(),
                "There isn't as much players as cards.");

        ArrayList<LGCard> cardsToDistribute = new ArrayList<>(cards);

        for (MutableLGPlayer player : playersByUUID.values()) {
            LGCard card = getRandomCardAndRemove(cardsToDistribute);
            player.setCard(card);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ImmutableSet<LGPlayer> getPlayers() {
        return ImmutableSet.copyOf(playersByUUID.values());
    }

    @Override
    public Optional<MutableLGPlayer> getPlayer(UUID playerUUID) {
        return Optional.ofNullable(playersByUUID.get(playerUUID));
    }

    @Override
    public Optional<MutableLGPlayer> getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    @Override
    public Optional<MutableLGPlayer> getPlayer(LGPlayer player) {
        return getPlayer(player.getPlayerUUID());
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

    @Override
    public Map<AnonymizedChatChannel, List<String>> getAnonymizedNames() {
        return anonymizedNames;
    }

    @Override
    public Optional<LGEnding> getEnding() {
        return Optional.ofNullable(ending);
    }

    public void setEnding(@Nullable LGEnding ending) {
        this.ending = ending;
    }
}
