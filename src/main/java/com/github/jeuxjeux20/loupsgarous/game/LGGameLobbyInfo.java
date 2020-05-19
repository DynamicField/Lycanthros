package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A set of data to create a new Loups-Garous game instance.
 */
public final class LGGameLobbyInfo {
    private final MultiverseWorld world;
    private final CommandSender initiator;
    private final UUID id;
    private final ImmutableSet<UUID> playerUUIDs;
    private final ImmutableList<LGCard> composition;

    public LGGameLobbyInfo(Set<UUID> playerUUIDs, List<LGCard> composition,
                           MultiverseWorld world, CommandSender initiator, UUID id) {
        this.playerUUIDs = ImmutableSet.copyOf(playerUUIDs);
        this.composition = new MutableComposition(playerUUIDs.size(), composition).getCards();
        this.world = world;
        this.initiator = initiator;
        this.id = id;
    }


    public MultiverseWorld getWorld() {
        return world;
    }

    public CommandSender getInitiator() {
        return initiator;
    }

    public UUID getId() {
        return id;
    }

    public ImmutableSet<UUID> getPlayerUUIDs() {
        return playerUUIDs;
    }

    public List<@Nullable Player> getPlayers() {
        return playerUUIDs.stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public ImmutableList<LGCard> getComposition() {
        return composition;
    }
}
