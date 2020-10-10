package com.github.jeuxjeux20.loupsgarous.teams;

import com.github.jeuxjeux20.loupsgarous.mechanic.Mechanic;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationRequest;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationResult;
import com.github.jeuxjeux20.loupsgarous.mechanic.SpecificMechanicModifier;

public abstract class TeamRevelationModifier
        extends SpecificMechanicModifier<RevelationRequest<LGTeam>, RevelationResult> {
    @Override
    protected final Mechanic<? extends RevelationRequest<LGTeam>, ? extends RevelationResult>
    getApplicableMechanic() {
        return LGTeam.REVELATION_MECHANIC;
    }
}
