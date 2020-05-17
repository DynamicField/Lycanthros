package com.github.df.loupsgarous.cards;

import com.github.df.loupsgarous.UserFriendlyNamed;
import com.github.df.loupsgarous.mechanic.RevelationMechanic;
import com.github.df.loupsgarous.mechanic.RevelationRequest;
import com.github.df.loupsgarous.mechanic.RevelationResult;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGGameState;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.powers.LGPower;
import com.github.df.loupsgarous.teams.LGTeam;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The base class for all Loups-Garous cards.
 */
public abstract class LGCard implements UserFriendlyNamed {
    public static final RevelationMechanic<LGCard> REVELATION_MECHANIC =
            new RevelationMechanic<LGCard>() {
                @Override
                public RevelationResult get(RevelationRequest<LGCard> request) {
                    if (request.getHolder().getCard() != request.getTarget()) {
                        return new RevelationResult(false);
                    } else if (request.getOrchestrator().getState() == LGGameState.FINISHED ||
                               request.getHolder() == request.getViewer() ||
                               request.getHolder().isDead()) {
                        return new RevelationResult(true);
                    }

                    return super.get(request);
                }

                @Override
                protected RevelationResult serve(RevelationRequest<LGCard> request) {
                    return new RevelationResult(request.getTarget().isRevealed(request));
                }
            };

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
        return REVELATION_MECHANIC.get(
                new RevelationRequest<>(orchestrator, holder, viewer, this)).isRevealed();
    }

    protected boolean isRevealed(RevelationRequest<LGCard> request) {
        return false;
    }
}
