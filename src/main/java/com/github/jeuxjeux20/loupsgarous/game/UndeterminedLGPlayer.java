package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.google.common.collect.ImmutableSet;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class UndeterminedLGPlayer implements LGPlayer {
    private final UUID playerUUID;

    public UndeterminedLGPlayer(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public @Nullable MultiverseWorld getPreviousWorld() {
        return null;
    }

    @Override
    public LGCard getCard() {
        return new LGCard() {
            @Override
            public String getName() {
                return "Inconnu";
            }

            @Override
            public String getPluralName() {
                return "Inconnus";
            }

            @Override
            public boolean isFeminineName() {
                return false;
            }

            @Override
            public ImmutableSet<String> getTeams() {
                return ImmutableSet.of();
            }

            @Override
            public ImmutableSet<String> getTags() {
                return ImmutableSet.of();
            }

            @Override
            public String getDescription() {
                return "?";
            }

            @Override
            public ChatColor getColor() {
                return ChatColor.RESET;
            }

            @Override
            public ItemStack createGuiItem() {
                return ItemStackBuilder.of(Material.BARRIER).build();
            }
        };
    }

    @Override
    public boolean isDead() {
        return false;
    }
}
