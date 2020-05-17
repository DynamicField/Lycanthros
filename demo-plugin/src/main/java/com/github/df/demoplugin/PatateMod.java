package com.github.df.demoplugin;

import com.github.df.loupsgarous.cards.LGCard;
import com.github.df.loupsgarous.extensibility.ItemProvider;
import com.github.df.loupsgarous.extensibility.Mod;
import com.github.df.loupsgarous.extensibility.ModInfo;
import com.github.df.loupsgarous.extensibility.registry.GameRegistries;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.inventory.InventoryItem;
import com.github.df.loupsgarous.powers.LGPower;
import com.github.df.loupsgarous.teams.LGTeam;
import com.github.df.loupsgarous.teams.LGTeams;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@ModInfo(name = "Patate mod !", item = PatateMod.ModItem.class)
public class PatateMod extends Mod {
    public PatateMod(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected void activate() {
        orchestrator.getGameRegistry(GameRegistries.CARDS)
                .register(PatateCard.INSTANCE)
                .bindWith(this);

        orchestrator.getGameRegistry(GameRegistries.INVENTORY_ITEMS)
                .registerMany(b -> b.register(new PatateItem(orchestrator))
                        .name("Patate")
                        .locatedBefore("QuitGame"))
                .bindWith(this);
    }
    
    public static class PatateItem extends InventoryItem {
        protected PatateItem(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public boolean isShown(LGPlayer player) {
            return true;
        }

        @Override
        public ItemStack render() {
            return new ItemStack(Material.POTATO);
        }

        @Override
        public void onClick(LGPlayer player) {
            player.sendMessage("Patate !");
        }
    }

    public static class PatateCard extends LGCard {
        public static final PatateCard INSTANCE = new PatateCard();

        private PatateCard() {
        }

        @Override
        protected LGTeam getMainTeam() {
            return LGTeams.VILLAGEOIS;
        }

        @Override
        public String getName() {
            return "Patate";
        }

        @Override
        public String getPluralName() {
            return "Patates";
        }

        @Override
        public boolean isFeminineName() {
            return true;
        }

        @Override
        public ImmutableSet<LGPower> createPowers() {
            return ImmutableSet.of();
        }

        @Override
        public String getDescription() {
            return "UNE PATATE MDR";
        }

        @Override
        public ItemStack createGuiItem() {
            return new ItemStack(Material.POTATO);
        }
    }

    public static final class ModItem implements ItemProvider {
        @Override
        public ItemStack get() {
            return new ItemStack(Material.POTATO);
        }
    }
}