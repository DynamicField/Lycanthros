package com.github.df.loupsgarous.cards;

import com.github.df.loupsgarous.cards.composition.validation.annotations.Unique;
import com.github.df.loupsgarous.powers.LGPower;
import com.github.df.loupsgarous.powers.VoyantePower;
import com.github.df.loupsgarous.teams.LGTeam;
import com.github.df.loupsgarous.teams.LGTeams;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

@Unique
public final class VoyanteCard extends LGCard {
    public static final VoyanteCard INSTANCE = new VoyanteCard();

    private VoyanteCard() {
    }

    @Override
    protected LGTeam getMainTeam() {
        return LGTeams.VILLAGEOIS;
    }

    @Override
    public String getName() {
        return "Voyante";
    }

    @Override
    public String getPluralName() {
        return "Voyantes";
    }

    @Override
    public boolean isFeminineName() {
        return true;
    }

    @Override
    public String getDescription() {
        return "La voyante peut voir le role de n'importe qui ! C'est vraiment broken.";
    }

    @Override
    public ImmutableSet<LGPower> createPowers() {
        return ImmutableSet.of(new VoyantePower(this));
    }

    @Override
    public ItemStack createGuiItem() {
        return ItemStackBuilder.of(Material.WHITE_BANNER)
                .transformMeta(m -> {
                    BannerMeta bannerMeta = (BannerMeta) m;

                    bannerMeta.addPattern(new Pattern(DyeColor.PURPLE, PatternType.GRADIENT));
                    bannerMeta.addPattern(new Pattern(DyeColor.PURPLE, PatternType.GRADIENT_UP));
                    bannerMeta.addPattern(new Pattern(DyeColor.PINK, PatternType.FLOWER));
                }).build();
    }
}
