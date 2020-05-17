package com.github.df.loupsgarous.cards;

import com.github.df.loupsgarous.mechanic.Mechanic;
import com.github.df.loupsgarous.mechanic.RevelationRequest;
import com.github.df.loupsgarous.mechanic.RevelationResult;
import com.github.df.loupsgarous.mechanic.SpecificMechanicModifier;

public abstract class CardRevelationModifier
        extends SpecificMechanicModifier<RevelationRequest<LGCard>, RevelationResult> {
    @Override
    protected final Mechanic<? extends RevelationRequest<LGCard>, ? extends RevelationResult>
    getApplicableMechanic() {
        return LGCard.REVELATION_MECHANIC;
    }
}
