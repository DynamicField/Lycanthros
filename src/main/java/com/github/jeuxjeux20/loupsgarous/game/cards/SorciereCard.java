package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.annotations.Unique;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.google.common.base.Preconditions;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

@Unique
public final class SorciereCard extends MutableLGCard {
    private boolean hasKillPotion = true;
    private boolean hasHealPotion = true;

    @Override
    protected LGTeam getMainTeam() {
        return LGTeams.VILLAGEOIS;
    }

    @Override
    public String getName() {
        return "Sorcière";
    }

    @Override
    public String getPluralName() {
        return "Sorcières";
    }

    @Override
    public boolean isFeminineName() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Elle doit tuer les loups-garous. La nuit, elle se réveille pour utiliser l'une de ses deux potions : " +
               "tuer quelqu'un ou sauver une personne des loups-garous. ";
    }

    public boolean hasKillPotion() {
        return hasKillPotion;
    }

    public void useKillPotion() {
        Preconditions.checkState(hasKillPotion, "No kill potion to use.");
        hasKillPotion = false;
    }

    public boolean hasHealPotion() {
        return hasHealPotion;
    }

    public void useHealPotion() {
        Preconditions.checkState(hasHealPotion, "No heal potion to use.");
        hasHealPotion = false;
    }

    @Override
    public ItemStack createGuiItem() {
        return ItemStackBuilder.of(Material.BLACK_BANNER)
                .transformMeta(m -> {
                    BannerMeta bannerMeta = (BannerMeta) m;

                    bannerMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CROSS));
                    bannerMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
                    bannerMeta.addPattern(new Pattern(DyeColor.RED, PatternType.STRIPE_BOTTOM));
                    bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
                    bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP));
                    bannerMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
                }).build();
    }
}
