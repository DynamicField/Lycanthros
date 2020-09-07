package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.cards.AbstractLGCard;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.CARDS;

@ModInfo(name = "Patate mod !")
@Singleton
public class PatateMod extends AbstractMod {
    @Override
    public List<Rule> createRules(LGGameOrchestrator orchestrator, ConfigurationNode configuration) {
        return ImmutableList.of(new PatateRule(orchestrator));
    }

    private static class PatateRule extends AbstractRule {
        public PatateRule(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public List<Extension<?>> getExtensions() {
            return ImmutableList.of(
                    extend(CARDS,
                            PatateCard.INSTANCE)
            );
        }

        @Override
        public String getName() {
            return "Patate";
        }
    }

    public static class PatateCard extends AbstractLGCard {
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
}