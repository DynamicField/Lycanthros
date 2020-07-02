package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTag;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.metadata.MetadataMap;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class MutableLGPlayer implements LGPlayer {
    private final UUID playerUUID;
    private final Set<LGTag> tags = new HashSet<>();
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

    @Override
    public ImmutableSet<LGTag> getTags() {
        return ImmutableSet.copyOf(tags);
    }

    public Set<LGTag> getMutableTags() {
        return tags;
    }

    @Override
    public MetadataMap metadata() {
        return LGMetadata.provideForPlayer(this);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("playerUUID", playerUUID)
                .add("tags", tags)
                .add("card", card)
                .add("isDead", isDead)
                .add("isAway", isAway)
                .toString();
    }
}
