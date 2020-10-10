package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.mechanic.Mechanic;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationRequest;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationResult;
import com.github.jeuxjeux20.loupsgarous.mechanic.SpecificMechanicModifier;

public abstract class CardRevelationModifier
        extends SpecificMechanicModifier<RevelationRequest<LGCard>, RevelationResult> {
    @Override
    protected final Mechanic<? extends RevelationRequest<LGCard>, ? extends RevelationResult>
    getApplicableMechanic() {
        return LGCard.REVELATION_MECHANIC;
    }
}
