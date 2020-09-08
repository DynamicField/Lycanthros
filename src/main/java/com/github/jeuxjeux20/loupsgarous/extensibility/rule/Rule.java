package com.github.jeuxjeux20.loupsgarous.extensibility.rule;

import com.github.jeuxjeux20.loupsgarous.extensibility.Extension;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorDependent;

import java.util.List;

public interface Rule extends OrchestratorDependent {
    List<Extension<?>> getExtensions();

    String getName();

    default boolean isEnabledByDefault() {
        return true;
    }

    boolean isEnabled();

    void enable();

    void disable();

    void addListener(RuleListener listener);

    void removeListener(RuleListener listener);
}
