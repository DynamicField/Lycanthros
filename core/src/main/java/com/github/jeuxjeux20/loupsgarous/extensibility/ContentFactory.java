package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public interface ContentFactory<T> {
    T create(LGGameOrchestrator orchestrator);
}
