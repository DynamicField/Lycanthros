package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.UserFriendlyNamed;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.CARD_REVELATION_MECHANICS;

/**
 * The base class for all Loups-Garous cards.
 */
public abstract class LGCard implements UserFriendlyNamed {
    /**
     * Gets the teams that this card is part of.
     *
     * @return an immutable set of teams
     */
    public ImmutableSet<LGTeam> getTeams() {
        if (getMainTeam() != null) {
            return ImmutableSet.of(getMainTeam());
        } else {
            return ImmutableSet.of();
        }
    }

    protected abstract @Nullable LGTeam getMainTeam();

    public ChatColor getColor() {
        return getMainTeam() == null ? ChatColor.WHITE : getMainTeam().getColor();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Gets the name of this card
     *
     * @return the name of this card
     */
    public abstract String getName();

    public abstract String getPluralName();

    public String getLowercasePluralName() {
        String pluralName = getPluralName();

        if (pluralName.length() <= 1) {
            return pluralName.toLowerCase();
        }

        return pluralName.substring(0, 1).toLowerCase() + pluralName.substring(1);
    }

    public abstract boolean isFeminineName();

    public abstract ImmutableSet<LGPower> createPowers();

    /**
     * Gets the description, shown at the start of the game, of this card.
     *
     * @return the description of this card
     */
    public abstract String getDescription();

    public abstract ItemStack createGuiItem();

    @Override
    public String getUserFriendlyName() {
        return getName();
    }

    public final boolean isRevealed(LGGameOrchestrator orchestrator,
                                    LGPlayer holder,
                                    LGPlayer viewer) {
        CardRevelationContext context =
                new CardRevelationContext(orchestrator, holder, viewer);

        if (holder.getCard() != this) {
            return false;
        } else if (orchestrator.getState() == LGGameState.FINISHED ||
                   holder == viewer ||
                   holder.isDead()) {
            return true;
        }

        setupRevelation(context);

        for (CardRevelationMechanic revelationMechanic :
                orchestrator.getGameBox().contents(CARD_REVELATION_MECHANICS)) {
            if (!context.isRevealed() || revelationMechanic.canHide()) {
                revelationMechanic.execute(context);
            }
        }

        return context.isRevealed();
    }

    protected void setupRevelation(CardRevelationContext context) {
        context.hide();
    }
}
