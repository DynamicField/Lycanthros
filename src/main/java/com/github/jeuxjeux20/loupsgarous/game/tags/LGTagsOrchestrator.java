package com.github.jeuxjeux20.loupsgarous.game.tags;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;

public interface LGTagsOrchestrator {
    boolean add(LGPlayer player, LGTag tag);

    boolean remove(LGPlayer player, LGTag tag);

    interface Factory {
        LGTagsOrchestrator create(MutableLGGameOrchestrator orchestrator);
    }
}
