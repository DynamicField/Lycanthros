package com.github.jeuxjeux20.loupsgarous.mechanic;

import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.ImmutableList;

import java.util.stream.Stream;

public interface MechanicModifier {
    MechanicModifier PIPELINE = (req, attr, result) -> {
        LGGameOrchestrator orchestrator = req.getOrchestrator();

        ImmutableList<MechanicModifier> modifiers = Stream.concat(
                LGExtensionPoints.MECHANIC_MODIFIERS.getContents(orchestrator).stream(),
                LGExtensionPoints.MECHANIC_MODIFIER_SOURCES.getContents(orchestrator).stream()
                        .flatMap(s -> s.get(req).stream())
        ).collect(ImmutableList.toImmutableList());

        for (MechanicModifier modifier : modifiers) {
            modifier.execute(req, attr, result);
        }
    };

    void execute(MechanicRequest request, Mechanic<?, ?> mechanic, Object result);
}
