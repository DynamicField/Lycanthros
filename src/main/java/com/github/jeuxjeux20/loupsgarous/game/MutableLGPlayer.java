package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MutableLGPlayer implements LGPlayer {
    private final UUID playerUUID;
    private final LGCard card;
    private boolean isDead;
    private boolean isAway;

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
