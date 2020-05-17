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

@CardName(singular = "Voyante", plural = "Voyantes", isFeminine = true)
@CardDescription("La voyante peut voir le role de n'importe qui ! C'est vraiment broken.")
@CardTeam(LGTeams.VILLAGEOIS)
@Unique
public final class VoyanteCard extends MutableLGCard implements AnnotatedLGCard {
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
