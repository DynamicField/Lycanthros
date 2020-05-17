package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MutableLGPlayer implements LGPlayer {
    private final @Nullable MultiverseWorld previousWorld;
    private final UUID playerUUID;
    private final LGCard card;
    private boolean isDead;

    public MutableLGPlayer(UUID playerUUID, @Nullable MultiverseWorld previousWorld, LGCard card) {
        this.playerUUID = playerUUID;
        this.previousWorld = previousWorld;
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
    public @Nullable MultiverseWorld getPreviousWorld() {
        return previousWorld;
    }
}
