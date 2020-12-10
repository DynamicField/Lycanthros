package com.github.jeuxjeux20.demoplugin;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.extensibility.Mod;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModInfo;
import com.github.jeuxjeux20.loupsgarous.extensibility.registry.GameRegistries;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@ModInfo(
        name = "Stupid mod",
        enabledByDefault = true
)
public class StupidMod extends Mod {
    public StupidMod(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected void activate() {
        orchestrator.getGameRegistry(GameRegistries.CARDS)
                .register(StupidCard.INSTANCE)
                .bindWith(this);
    }

    public static class StupidCard extends LGCard {
        public static final StupidCard INSTANCE = new StupidCard();

        private StupidCard() {
        }

        @Override
        protected LGTeam getMainTeam() {
            return LGTeams.VILLAGEOIS;
        }

        @Override
        public String getName() {
            return "Stupide";
        }

        @Override
        public String getPluralName() {
            return "Stupides";
        }

        @Override
        public boolean isFeminineName() {
            return false;
        }

        @Override
        public ImmutableSet<LGPower> createPowers() {
            return ImmutableSet.of();
        }

        @Override
        public String getDescription() {
            return "Une carte stupide ! C'est tout";
        }

        @Override
        public ItemStack createGuiItem() {
            return new ItemStack(Material.ACACIA_BOAT);
        }
    }
}
