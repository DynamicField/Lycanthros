package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class MutableLGPlayer implements LGPlayer {
    private final UUID playerUUID;
    private LGCard card;
    private boolean isDead;
    private boolean isAway;

    public MutableLGPlayer(Player player) {
        this(player.getUniqueId(), new LGCard.Unknown());
    }

    public MutableLGPlayer(UUID playerUUID, LGCard card) {
        this.playerUUID = playerUUID;
        this.card = card;
    }

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public LGCard getCard() {
        return card;
    }

    public void setCard(LGCard card) {
        this.card = card;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    @Override
    public boolean isAway() {
        return isAway;
    }

    public void setAway(boolean isAway) {
        this.isAway = isAway;
    }
}
