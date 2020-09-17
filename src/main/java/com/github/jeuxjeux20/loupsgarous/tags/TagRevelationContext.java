package com.github.jeuxjeux20.loupsgarous.tags;

import com.github.jeuxjeux20.loupsgarous.RevelationContext;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public class TagRevelationContext extends RevelationContext {
    private final LGTag tag;

    protected TagRevelationContext(LGGameOrchestrator orchestrator,
                                   LGPlayer viewer, LGPlayer holder, LGTag tag) {
        super(orchestrator, viewer, holder);
        this.tag = tag;
    }

    public LGTag getTag() {
        return tag;
    }
}
