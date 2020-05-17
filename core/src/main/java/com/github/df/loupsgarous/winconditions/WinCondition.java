package com.github.df.loupsgarous.winconditions;

import com.github.df.loupsgarous.endings.LGEnding;
import com.github.df.loupsgarous.game.LGGameOrchestrator;

import java.util.Optional;

public interface WinCondition {
    Optional<LGEnding> check(LGGameOrchestrator orchestrator);
}
