package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.annotations.Unique;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

@Unique
public final class ChasseurCard extends MutableLGCard {
    @Override
    protected String getMainTeam() {
        return LGTeams.VILLAGEOIS;
    }

    @Override
    public String getName() {
        return "Chasseur";
    }

    @Override
    public String getPluralName() {
        return "Chasseurs";
    }

    @Override
    public boolean isFeminineName() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Il doit tuer les loups-garous. Juste avant de mourir, il peut tirer une balle sur la personne " +
                "de son choix. Nice.";
    }

    @Override
    public ItemStack createGuiItem() {
        return ItemStackBuilder.of(Material.WHITE_BANNER)
                .transformMeta(m -> {
                    BannerMeta bannerMeta = (BannerMeta) m;

                    bannerMeta.addPattern(new Pattern(DyeColor.LIME, PatternType.FLOWER));
                    bannerMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.BORDER));
                }).build();
    }
}
