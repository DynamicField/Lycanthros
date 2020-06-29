package com.github.jeuxjeux20.loupsgarous.game.event.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Pick;
import com.google.common.base.Preconditions;

public abstract class LGPickEventBase extends LGEvent {
    private final Pick<?, ?> pick;

    public LGPickEventBase(LGGameOrchestrator orchestrator, Pick<?, ?> pick) {
        super(orchestrator);

        Preconditions.checkArgument(orchestrator.interactables().has(pick.getEntry()),
                "The interactable entry " + pick.getEntry() + " is not present.");

        this.pick = pick;
    }

    public Pick<?, ?> getPick() {
        return pick;
    }
}
