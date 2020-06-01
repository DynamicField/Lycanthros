package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public final class LoupGarouCard extends MutableLGCard implements AnonymousNameHolder {
    private transient String anonymizedName;

    @Override
    public String getName() {
        return "Loup-garou";
    }

    @Override
    public String getPluralName() {
        return "Loups-garous";
    }

    @Override
    public boolean isFeminineName() {
        return false;
    }

    @Override
    protected LGTeam getMainTeam() {
        return LGTeams.LOUPS_GAROUS;
    }

    @Override
    public String getDescription() {
        return "Wouf ! C'est LE loup-garou. Son but ? Tuer tous les innocents. Voilà.";
    }

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
                    BannerMeta bannerMeta = (BannerMeta) m;

                    bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.RHOMBUS_MIDDLE));
                    bannerMeta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.CREEPER));
                    bannerMeta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.CURLY_BORDER));
                    bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.CURLY_BORDER));
                    bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.TRIANGLE_TOP));
                }).build();
    }
}
