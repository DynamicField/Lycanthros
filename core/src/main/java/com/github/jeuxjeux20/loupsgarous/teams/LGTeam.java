package com.github.jeuxjeux20.loupsgarous.teams;

import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationMechanic;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationRequest;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationResult;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.bukkit.ChatColor;

public abstract class LGTeam {
    public static final RevelationMechanic<LGTeam> REVELATION_MECHANIC =
            new RevelationMechanic<LGTeam>() {
                @Override
                public RevelationResult get(RevelationRequest<LGTeam> request) {
                    if (!request.getHolder().teams().has(request.getTarget())) {
                        return new RevelationResult(false);
                    }

                    return super.get(request);
                }

                @Override
                protected RevelationResult serve(RevelationRequest<LGTeam> request) {
                    return new RevelationResult(request.getTarget().isRevealed(request));
                }
            };

    public abstract String getName();

    public abstract ChatColor getColor();

    public final boolean isRevealed(LGGameOrchestrator orchestrator,
                                    LGPlayer holder, LGPlayer viewer) {
        return REVELATION_MECHANIC.get(
                new RevelationRequest<>(orchestrator, holder, viewer, this)).isRevealed();
    }

    protected boolean isRevealed(RevelationRequest<LGTeam> request) {
        return false;
    }
}
