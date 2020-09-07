package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

import java.util.List;

public interface Rule {
    List<Extension<?>> getExtensions();

    LGGameOrchestrator getOrchestrator();

    String getName();

    boolean isEnabled();

    void enable();

    void disable();

    void addListener(RuleListener listener);

    boolean removeListener(RuleListener listener);
}
