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

@CardName(singular = "Loup-garou", plural = "Loups-garous", isFeminine = false)
@CardDescription("Wouf ! C'est LE loup-garou. Son but ? Tuer tous les innocents. Voilà.")
@CardTeam(LGTeams.LOUPS_GAROUS)
public final class LoupGarouCard extends MutableLGCard implements AnnotatedLGCard, AnonymousNameHolder {
    private transient String anonymizedName;

    public String getAnonymizedName() {
        return anonymizedName;
    }

    public void setAnonymizedName(String anonymizedName) {
        this.anonymizedName = anonymizedName;
    }

    @Override
    public ItemStack createGuiItem() {
        return ItemStackBuilder.of(Material.WHITE_BANNER)
                .transformMeta(m -> {
                    BannerMeta bannerMeta = (BannerMeta)m;

                    bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.RHOMBUS_MIDDLE));
                    bannerMeta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.CREEPER));
                    bannerMeta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.CURLY_BORDER));
                    bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.CURLY_BORDER));
                    bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.TRIANGLE_TOP));
                }).build();
    }
}
