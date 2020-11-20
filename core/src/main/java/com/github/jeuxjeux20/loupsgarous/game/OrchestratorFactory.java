package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.lobby.LGGameBootstrapData;

interface OrchestratorFactory {
    LGGameOrchestrator create(LGGameBootstrapData data) throws GameCreationException;
}
