package com.github.df.loupsgarous.mechanic;

import com.github.df.loupsgarous.extensibility.registry.GameRegistries;
import com.github.df.loupsgarous.game.LGGameOrchestrator;

public interface MechanicModifier {
    MechanicModifier PIPELINE = (req, attr, result) -> {
        LGGameOrchestrator orchestrator = req.getOrchestrator();

        for (MechanicModifier modifier : GameRegistries.MECHANIC_MODIFIERS.get(orchestrator)) {
            modifier.execute(req, attr, result);
        }
    };

    void execute(MechanicRequest request, Mechanic<?, ?> mechanic, Object result);
}
