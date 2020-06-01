package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.annotations.Unique;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.HashSet;
import java.util.Set;

@Unique
public final class VoyanteCard extends MutableLGCard {
    private final Set<LGPlayer> playersSaw = new HashSet<>();

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
    public ItemStack createGuiItem() {
        return ItemStackBuilder.of(Material.WHITE_BANNER)
                .transformMeta(m -> {
                    BannerMeta bannerMeta = (BannerMeta) m;

                    bannerMeta.addPattern(new Pattern(DyeColor.PURPLE, PatternType.GRADIENT));
                    bannerMeta.addPattern(new Pattern(DyeColor.PURPLE, PatternType.GRADIENT_UP));
                    bannerMeta.addPattern(new Pattern(DyeColor.PINK, PatternType.FLOWER));
                }).build();
    }

    public Set<LGPlayer> getPlayersSaw() {
        return playersSaw;
    }
}
