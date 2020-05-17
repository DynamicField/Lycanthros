package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.cards.annotations.CardDescription;
import com.github.jeuxjeux20.loupsgarous.game.cards.annotations.CardName;
import com.github.jeuxjeux20.loupsgarous.game.cards.annotations.CardTeam;
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
@CardName(singular = "Simple villageois", plural = "Simples villageois", isFeminine = false)
@CardDescription("Il doit tuer les loups-garous. Le villageois n'a aucun pouvoir. Wow.")
@CardTeam(LGTeams.VILLAGEOIS)
public final class VillageoisCard extends MutableLGCard implements AnnotatedLGCard {
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
