package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.distribution.CardDistributor;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.powers.LGPower;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.metadata.MetadataMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MutableLGGame implements LGGame {
    private final String id;
    private final Map<UUID, MutableLGPlayer> playersByUUID = new HashMap<>();
    private final MutableLGGameTurn turn = new MutableLGGameTurn();
    private final MetadataMap metadataMap = MetadataMap.create();

    private LGGameState state = LGGameState.UNINITIALIZED;
    private @Nullable LGEnding ending;
    private MutableLGPlayer owner;

    public MutableLGGame(String id) {
        this.id = id;
    }

    public void distributeCards(CardDistributor distributor, Composition composition) {
        for (Map.Entry<LGPlayer, LGCard> entry : distributor.distribute(composition, getPlayers()).entrySet()) {
            LGCard card = entry.getValue();
            MutableLGPlayer player = ensurePresent(entry.getKey());

            player.setCard(card);
            player.getMutableTeams().addAll(card.getTeams());
            for (LGPower power : card.createPowers()) {
                player.getMutablePowers().put(power.getClass(), power);
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public LGGameState getState() {
        return state;
    }

    // Package private because we don't want anyone else than the orchestrator
    // to tinker with it
    void setState(LGGameState state) {
        this.state = state;
    }

    @Override
    public ImmutableSet<LGPlayer> getPlayers() {
        return ImmutableSet.copyOf(playersByUUID.values());
    }

    @Override
    public MutableLGGameTurn getTurn() {
        return turn;
    }

    @Override
    public @Nullable LGEnding getEnding() {
        return ending;
    }

    public void setEnding(@Nullable LGEnding ending) {
        this.ending = ending;
    }

    @Override
    public MutableLGPlayer getOwner() {
        return owner;
    }

    @Override
    public MetadataMap getMetadata() {
        return metadataMap;
    }

    public void setOwner(MutableLGPlayer owner) {
        ensurePresent(owner);
        this.owner = owner;
    }

    @Override
    public Optional<MutableLGPlayer> getPlayer(UUID playerUUID) {
        return Optional.ofNullable(playersByUUID.get(playerUUID));
    }

    @Override
    public MutableLGPlayer getPlayerOrThrow(UUID playerUUID) {
        MutableLGPlayer player = playersByUUID.get(playerUUID);
        if (player == null) {
            throw new PlayerAbsentException(
                    "The given player UUID " + playerUUID +
                    " is not present in game " + this);
        }
        return player;
    }

    @Override
    public MutableLGPlayer ensurePresent(LGPlayer player) {
        if (!(player instanceof MutableLGPlayer) || !playersByUUID.containsValue(player)) {
            throw new PlayerAbsentException(
                    "The given player " + player + " is not present in game " + this);
        }
        return (MutableLGPlayer) player;
    }

    public void addPlayer(MutableLGPlayer player) {
        if (playersByUUID.containsValue(player)) {
            throw new IllegalArgumentException("This player is already present.");
        }

        playersByUUID.put(player.getPlayerUUID(), player);
    }

    public void removePlayer(UUID playerUUID) {
        if (owner.getPlayerUUID() == playerUUID) {
            owner = null;
        }
        playersByUUID.remove(playerUUID);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("turn", turn)
                .add("state", state)
                .add("ending", ending)
                .add("owner", owner)
                .toString();
    }
}
