package com.github.jeuxjeux20.loupsgarous.phases.overrides;

public class LGPhaseOverridesModule extends PhaseOverridesModule {
    @Override
    protected void configurePhaseOverrides() {
        addPhaseOverride(GameStartPhaseOverride.class);
        addPhaseOverride(GameEndPhaseOverride.class);
    }
}
