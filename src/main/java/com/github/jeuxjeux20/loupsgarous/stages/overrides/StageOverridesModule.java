package com.github.jeuxjeux20.loupsgarous.stages.overrides;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class StageOverridesModule extends AbstractModule {
    private @Nullable Multibinder<StageOverride> stageOverrideBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureStageOverrides();
    }

    protected void configureBindings() {
    }

    protected void configureStageOverrides() {
    }

    private void actualConfigureStageOverrides() {
        stageOverrideBinder = Multibinder.newSetBinder(binder(), StageOverride.class);

        configureStageOverrides();
    }

    protected final void addStageOverride(Class<? extends StageOverride> stageOverride) {
        addStageOverride(TypeLiteral.get(stageOverride));
    }

    protected final void addStageOverride(TypeLiteral<? extends StageOverride> stageOverride) {
        Preconditions.checkState(stageOverrideBinder != null, "addStageOverride can only be used inside configureStageOverrides()");

        stageOverrideBinder.addBinding().to(stageOverride);
    }
}
