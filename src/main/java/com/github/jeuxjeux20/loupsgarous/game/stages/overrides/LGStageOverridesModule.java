package com.github.jeuxjeux20.loupsgarous.game.stages.overrides;

public class LGStageOverridesModule extends StageOverridesModule {
    @Override
    protected void configureStageOverrides() {
        addStageOverride(GameStartStageOverride.class);
        addStageOverride(GameEndStageOverride.class);
    }
}
