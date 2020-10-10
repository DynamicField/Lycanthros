package com.github.jeuxjeux20.loupsgarous.tags;

import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationMechanic;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationRequest;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationResult;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.bukkit.ChatColor;

public abstract class LGTag {
    public static final RevelationMechanic<LGTag> REVELATION_MECHANIC =
            new RevelationMechanic<LGTag>() {
                @Override
                public RevelationResult get(RevelationRequest<LGTag> request) {
                    if (!request.getHolder().tags().has(request.getTarget())) {
                        return new RevelationResult(false);
                    }

                    return super.get(request);
                }

                @Override
                protected RevelationResult serve(RevelationRequest<LGTag> request) {
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

    protected boolean isRevealed(RevelationRequest<LGTag> request) {
        return false;
    }
}
