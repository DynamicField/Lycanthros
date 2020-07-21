package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTag;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import me.lucko.helper.metadata.MetadataMap;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class BackingLGPlayer {
    private final UUID playerUUID;
    private final Set<LGTag> tags = new HashSet<>();
    private final Set<LGTeam> teams = new HashSet<>();
    private final ClassToInstanceMap<LGPower> powers = MutableClassToInstanceMap.create();
    private LGCard card;
    private boolean isDead;
    private boolean isAway;

    private final MetadataMap metadataMap = MetadataMap.create();

    public BackingLGPlayer(Player player) {
        this(player.getUniqueId(), new LGCard.Unknown());
    }

    public BackingLGPlayer(UUID playerUUID, LGCard card) {
        this.playerUUID = playerUUID;
        this.card = card;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public LGCard getCard() {
        return card;
    }

    public void setCard(LGCard card) {
        this.card = card;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean isAway() {
        return isAway;
    }

    public void setAway(boolean isAway) {
        this.isAway = isAway;
    }

    public Set<LGTag> getTags() {
        return tags;
    }

    public Set<LGTeam> getTeams() {
        return teams;
    }

    public ClassToInstanceMap<LGPower> getPowers() {
        return powers;
    }

    public MetadataMap metadata() {
        return metadataMap;
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
