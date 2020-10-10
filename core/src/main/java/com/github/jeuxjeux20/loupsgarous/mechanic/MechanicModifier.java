package com.github.jeuxjeux20.loupsgarous.mechanic;

import com.github.jeuxjeux20.loupsgarous.extensibility.GameBox;
import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
import com.google.common.collect.ImmutableList;

import java.util.stream.Stream;

public interface MechanicModifier {
    MechanicModifier PIPELINE = (req, attr, result) -> {
        GameBox gameBox = req.getOrchestrator().getGameBox();

        ImmutableList<MechanicModifier> modifiers = Stream.concat(
                gameBox.contents(LGExtensionPoints.MECHANIC_MODIFIERS).stream(),
                gameBox.contents(LGExtensionPoints.MECHANIC_MODIFIER_SOURCES).stream()
                        .flatMap(s -> s.get(req).stream())
        ).collect(ImmutableList.toImmutableList());

        for (MechanicModifier modifier : modifiers) {
            modifier.execute(req, attr, result);
        }
    };

    void execute(MechanicRequest request, Mechanic<?, ?> mechanic, Object result);
}
