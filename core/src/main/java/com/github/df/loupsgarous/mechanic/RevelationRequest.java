package com.github.df.loupsgarous.mechanic;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;

public class RevelationRequest<T> extends MechanicRequest {
    private final LGPlayer holder;
    private final LGPlayer viewer;
    private final T target;

    public RevelationRequest(LGGameOrchestrator orchestrator, LGPlayer holder, LGPlayer viewer, T target) {
        super(orchestrator);
        this.holder = holder;
        this.viewer = viewer;
        this.target = target;
    }

    public LGPlayer getHolder() {
        return holder;
    }

    public LGPlayer getViewer() {
        return viewer;
    }

    public T getTarget() {
        return target;
    }
}
