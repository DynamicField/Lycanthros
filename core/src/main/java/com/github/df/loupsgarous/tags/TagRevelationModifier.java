package com.github.df.loupsgarous.tags;

import com.github.df.loupsgarous.mechanic.Mechanic;
import com.github.df.loupsgarous.mechanic.RevelationRequest;
import com.github.df.loupsgarous.mechanic.RevelationResult;
import com.github.df.loupsgarous.mechanic.SpecificMechanicModifier;

public abstract class TagRevelationModifier
        extends SpecificMechanicModifier<RevelationRequest<LGTag>, RevelationResult> {
    @Override
    protected final Mechanic<? extends RevelationRequest<LGTag>, ? extends RevelationResult>
    getApplicableMechanic() {
        return LGTag.REVELATION_MECHANIC;
    }
}
