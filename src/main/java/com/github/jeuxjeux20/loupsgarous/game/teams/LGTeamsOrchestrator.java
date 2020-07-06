package com.github.jeuxjeux20.loupsgarous.game.teams;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface LGTeamsOrchestrator extends LGGameOrchestratorDependent {
    boolean add(LGPlayer player, LGTeam team);

    boolean remove(LGPlayer player, LGTeam team);
}
