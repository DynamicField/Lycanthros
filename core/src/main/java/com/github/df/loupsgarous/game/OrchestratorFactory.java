package com.github.df.loupsgarous.game;

import com.github.df.loupsgarous.lobby.LGGameBootstrapData;

interface OrchestratorFactory {
    LGGameOrchestrator create(LGGameBootstrapData data) throws GameCreationException;
}
