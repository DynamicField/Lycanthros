package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.PickData;

public abstract class LGPickEventBase extends LGEvent {
    private final PickData<?, ?> pickData;

    public LGPickEventBase(LGGameOrchestrator orchestrator, PickData<?, ?> pickData) {
        super(orchestrator);

        this.pickData = pickData;
    }

    public PickData<?, ?> getPickData() {
        return pickData;
    }
}
