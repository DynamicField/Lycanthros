package com.github.df.loupsgarous.cards;

import com.github.df.loupsgarous.powers.LGPower;
import com.github.df.loupsgarous.teams.LGTeam;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class UnknownCard extends LGCard {
    public static final UnknownCard INSTANCE = new UnknownCard();

    private UnknownCard() {
    }

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
    protected LGTeam getMainTeam() {
        return null;
    }

    @Override
    public ImmutableSet<LGPower> createPowers() {
        return ImmutableSet.of();
    }

    @Override
    public String getDescription() {
        return "?";
    }

    @Override
    public ItemStack createGuiItem() {
        return ItemStackBuilder.of(Material.BARRIER).build();
    }
}
