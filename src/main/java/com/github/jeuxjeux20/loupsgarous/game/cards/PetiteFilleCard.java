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
public final class PetiteFilleCard extends MutableLGCard implements LoupGarouNightSpy {
    @Override
    public String getName() {
        return "Petite fille";
    }

    @Override
    public String getPluralName() {
        return "Petites filles";
    }

    @Override
    public boolean isFeminineName() {
        return true;
    }

    @Override
    protected String getMainTeam() {
        return LGTeams.VILLAGEOIS;
    }

    @Override
    public String getDescription() {
        return "La voyante peut espionner ce que disent les loups-garous.";
    }

    @Override
    public ItemStack createGuiItem() {
        return ItemStackBuilder.of(Material.BLACK_BANNER)
                .transformMeta(m -> {
                    BannerMeta bannerMeta = (BannerMeta) m;

                    bannerMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(DyeColor.BLUE, PatternType.GRADIENT_UP));
                    bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.GRADIENT));
                    bannerMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CIRCLE_MIDDLE));
                    bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_LEFT));
                    bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_BOTTOM));
                }).build();
    }
}
