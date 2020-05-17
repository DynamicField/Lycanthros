package com.github.df.loupsgarous.extensibility;

import com.github.df.loupsgarous.game.LGGameOrchestrator;

public interface ContentFactory<T> {
    T create(LGGameOrchestrator orchestrator);
}
