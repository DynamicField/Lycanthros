package com.github.jeuxjeux20.loupsgarous.stages.overrides;

public class LGStageOverridesModule extends StageOverridesModule {
    @Override
    protected void configureStageOverrides() {
        addStageOverride(GameStartStageOverride.class);
        addStageOverride(GameEndStageOverride.class);
    }
}
