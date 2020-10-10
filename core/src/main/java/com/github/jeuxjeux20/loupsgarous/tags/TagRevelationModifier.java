package com.github.jeuxjeux20.loupsgarous.tags;

import com.github.jeuxjeux20.loupsgarous.mechanic.Mechanic;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationRequest;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationResult;
import com.github.jeuxjeux20.loupsgarous.mechanic.SpecificMechanicModifier;

public abstract class TagRevelationModifier
        extends SpecificMechanicModifier<RevelationRequest<LGTag>, RevelationResult> {
    @Override
    protected final Mechanic<? extends RevelationRequest<LGTag>, ? extends RevelationResult>
    getApplicableMechanic() {
        return LGTag.REVELATION_MECHANIC;
    }
}
