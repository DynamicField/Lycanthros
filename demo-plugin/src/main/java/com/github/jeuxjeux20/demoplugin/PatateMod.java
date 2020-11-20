package com.github.jeuxjeux20.demoplugin;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.extensibility.*;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.inventory.InventoryItem;
import com.github.jeuxjeux20.loupsgarous.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

@ModInfo(name = "Patate mod !", item = PatateMod.ModItem.class)
public class PatateMod extends Mod {
    @Override
    public List<Rule> createRules(LGGameOrchestrator orchestrator, ConfigurationNode configuration) {
        return ImmutableList.of(new PatateRule(orchestrator));
    }

    private static class PatateRule extends CardRule {
        public PatateRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public LGCard getCard() {
            return PatateCard.INSTANCE;
        }

        @Override
        protected List<Extension<?>> getOtherExtensions() {
            return Extension.listBuilder()
                    .extend(LGExtensionPoints.INVENTORY_ITEMS, b ->
                            b.add(new PatateItem())
                                    .id("Patate")
                                    .locatedBefore("QuitGame"))
                    .build();
        }
    }

    public static class PatateItem implements InventoryItem {
        @Override
        public boolean isShown(LGPlayer player, LGGameOrchestrator orchestrator) {
            return true;
        }

        @Override
        public ItemStack getItemStack() {
            return new ItemStack(Material.POTATO);
        }

        @Override
        public void onClick(LGPlayer player, LGGameOrchestrator orchestrator) {
            player.sendMessage("Bah ça marche en fait.");
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