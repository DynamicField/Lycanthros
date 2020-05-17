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

@CardName(singular = "Chasseur", plural = "Chasseurs", isFeminine = false)
@CardDescription("Il doit tuer les loups-garous. Juste avant de mourir, il peut tirer une balle sur la personne " +
                 "de son choix. Nice.")
@CardTeam(LGTeams.VILLAGEOIS)
@Unique
public final class ChasseurCard extends MutableLGCard implements AnnotatedLGCard {
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
