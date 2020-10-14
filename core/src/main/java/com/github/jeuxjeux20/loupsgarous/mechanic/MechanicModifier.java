package com.github.jeuxjeux20.loupsgarous.mechanic;

import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.ImmutableList;

public interface MechanicModifier {
    MechanicModifier PIPELINE = (req, attr, result) -> {
        LGGameOrchestrator orchestrator = req.getOrchestrator();
        ImmutableList<MechanicModifier> modifiers =
                LGExtensionPoints.MECHANIC_MODIFIERS.getContents(orchestrator);

        for (MechanicModifier modifier : modifiers) {
            modifier.execute(req, attr, result);
        }
    };

    void execute(MechanicRequest request, Mechanic<?, ?> mechanic, Object result);
}
