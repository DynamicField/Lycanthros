package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorComponent;

public interface LGCardsOrchestrator extends LGGameOrchestratorComponent {
    boolean addTeam(LGCard card, String team);

    boolean removeTeam(LGCard card, String team);

    interface Factory {
        LGCardsOrchestrator create(LGGameOrchestrator orchestrator);
    }
}
