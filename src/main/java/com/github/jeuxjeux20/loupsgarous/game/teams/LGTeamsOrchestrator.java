package com.github.jeuxjeux20.loupsgarous.game.teams;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;

public interface LGTeamsOrchestrator extends LGGameOrchestratorDependent {
    boolean add(LGPlayer player, LGTeam team);

    boolean remove(LGPlayer player, LGTeam team);

    interface Factory {
        LGTeamsOrchestrator create(MutableLGGameOrchestrator orchestrator);
    }
}
