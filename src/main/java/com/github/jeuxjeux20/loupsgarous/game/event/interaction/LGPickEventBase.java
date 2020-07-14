package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.PickData;
import com.google.common.base.Preconditions;

public abstract class LGPickEventBase extends LGEvent {
    private final PickData<?, ?> pickData;

    public LGPickEventBase(LGGameOrchestrator orchestrator, PickData<?, ?> pickData) {
        super(orchestrator);

        Preconditions.checkArgument(orchestrator.interactables().has(pickData.getEntry()),
                "The interactable entry " + pickData.getEntry() + " is not present.");

        this.pickData = pickData;
    }

    public PickData<?, ?> getPickData() {
        return pickData;
    }
}
