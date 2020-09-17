package com.github.jeuxjeux20.loupsgarous.teams;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.bukkit.ChatColor;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.TEAM_REVELATION_MECHANICS;

public abstract class LGTeam {
    public abstract String getName();

    public abstract ChatColor getColor();

    public final boolean isRevealed(LGGameOrchestrator orchestrator,
                                    LGPlayer holder, LGPlayer viewer) {
        TeamRevelationContext context =
                new TeamRevelationContext(orchestrator, holder, viewer, this);

        if (!holder.teams().has(this)) {
            return false;
        }

        setupRevelation(context);

        for (TeamRevelationMechanic revelationMechanic :
                orchestrator.getGameBox().contents(TEAM_REVELATION_MECHANICS)) {
            if (revelationMechanic.handlesTeam(this) &&
                (!context.isRevealed() || revelationMechanic.canHide())) {
                revelationMechanic.execute(context);
            }
        }

        return context.isRevealed();
    }

    protected void setupRevelation(TeamRevelationContext context) {
        context.hide();
    }
}
