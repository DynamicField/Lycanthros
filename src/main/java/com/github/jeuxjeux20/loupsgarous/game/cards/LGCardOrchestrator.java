package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public interface LGCardOrchestrator {
    LGGameOrchestrator gameOrchestrator();

    boolean addTeam(LGCard card, String team);

    boolean removeTeam(LGCard card, String team);

    interface Factory {
        LGCardOrchestrator create(LGGameOrchestrator orchestrator);
    }
}
