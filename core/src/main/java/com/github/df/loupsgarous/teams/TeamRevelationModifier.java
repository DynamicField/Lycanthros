package com.github.df.loupsgarous.teams;

import com.github.df.loupsgarous.mechanic.Mechanic;
import com.github.df.loupsgarous.mechanic.RevelationRequest;
import com.github.df.loupsgarous.mechanic.RevelationResult;
import com.github.df.loupsgarous.mechanic.SpecificMechanicModifier;

public abstract class TeamRevelationModifier
        extends SpecificMechanicModifier<RevelationRequest<LGTeam>, RevelationResult> {
    @Override
    protected final Mechanic<? extends RevelationRequest<LGTeam>, ? extends RevelationResult>
    getApplicableMechanic() {
        return LGTeam.REVELATION_MECHANIC;
    }
}
