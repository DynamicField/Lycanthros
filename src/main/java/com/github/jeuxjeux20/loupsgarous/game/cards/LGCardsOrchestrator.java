package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;

public interface LGCardsOrchestrator extends LGGameOrchestratorComponent {
    boolean addTeam(LGCard card, LGTeam team);

    boolean removeTeam(LGCard card, LGTeam team);

    interface Factory {
        LGCardsOrchestrator create(LGGameOrchestrator orchestrator);
    }
}
