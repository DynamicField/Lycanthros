package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

/**
 * La carte du villageois.
 * <p>
 * <img src="doc-files/villageois.jpg" width="128">
 */
public final class VillageoisCard extends AbstractLGCard {
    public static final VillageoisCard INSTANCE = new VillageoisCard();

    private VillageoisCard() {
    }

    @Override
    public String getName() {
        return "Simple villageois";
    }

    @Override
    public String getPluralName() {
        return "Simples villageois";
    }

    @Override
    public boolean isFeminineName() {
        return false;
    }

    @Override
    protected LGTeam getMainTeam() {
        return LGTeams.VILLAGEOIS;
    }

    @Override
    public String getDescription() {
        return "Il doit tuer les loups-garous. Le villageois n'a aucun pouvoir. Wow.";
    }

    @Override
    public ImmutableSet<LGPower> createPowers() {
        return ImmutableSet.of();
    }

    @Override
    public ItemStack createGuiItem() {
        return ItemStackBuilder.of(Material.YELLOW_BANNER)
                .transformMeta(m -> {
                    BannerMeta bannerMeta = (BannerMeta) m;

                    bannerMeta.addPattern(new Pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.FLOWER));
                }).build();
    }
}
