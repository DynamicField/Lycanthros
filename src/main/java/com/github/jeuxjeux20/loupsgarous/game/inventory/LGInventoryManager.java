package com.github.jeuxjeux20.loupsgarous.game.inventory;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface LGInventoryManager {
    // TODO: Remove the orchestrator parameter.
    void update(LGPlayer player, LGGameOrchestrator orchestrator);
}
