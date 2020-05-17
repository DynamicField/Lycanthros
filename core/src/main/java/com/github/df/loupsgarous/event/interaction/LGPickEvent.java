package com.github.df.loupsgarous.event.interaction;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.event.LGEvent;
import com.github.df.loupsgarous.interaction.PickData;

public abstract class LGPickEvent extends LGEvent {
    private final PickData<?> pickData;

    public LGPickEvent(LGGameOrchestrator orchestrator, PickData<?> pickData) {
        super(orchestrator);

        this.pickData = pickData;
    }

    public PickData<?> getPickData() {
        return pickData;
    }
}
