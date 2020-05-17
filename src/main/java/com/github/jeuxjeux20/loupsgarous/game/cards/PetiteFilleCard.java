package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.cards.annotations.CardDescription;
import com.github.jeuxjeux20.loupsgarous.game.cards.annotations.CardName;
import com.github.jeuxjeux20.loupsgarous.game.cards.annotations.CardTeam;
import com.github.jeuxjeux20.loupsgarous.game.composition.validation.annotations.Unique;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

@CardName(singular = "Petite fille", plural = "Petites filles", isFeminine = true)
@CardDescription("Elle doit tuer les loups garous. " +
                 "La petite fille se lève la nuit pour espionner ce que disent les loups-garous.")
@CardTeam(LGTeams.VILLAGEOIS)
@Unique
public final class PetiteFilleCard extends MutableLGCard implements AnnotatedLGCard, LoupGarouNightSpy {
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
