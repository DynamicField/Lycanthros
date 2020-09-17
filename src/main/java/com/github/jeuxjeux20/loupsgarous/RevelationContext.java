package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public abstract class RevelationContext {
    private final LGGameOrchestrator orchestrator;
    private final LGPlayer viewer;
    private final LGPlayer holder;
    private boolean revealed;

    protected RevelationContext(LGGameOrchestrator orchestrator, LGPlayer viewer, LGPlayer holder) {
        if (viewer.getOrchestrator() != orchestrator ||
            holder.getOrchestrator() != orchestrator) {
            throw new IllegalArgumentException(
                    "The viewer or the holder does not belong in the specified orchestrator.");
        }

        this.orchestrator = orchestrator;
        this.viewer = viewer;
        this.holder = holder;
    }

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public void reveal() {
        setRevealed(true);
    }

    public void hide() {
        setRevealed(false);
    }

    public LGPlayer getViewer() {
        return viewer;
    }

    public LGPlayer getHolder() {
        return holder;
    }
}
