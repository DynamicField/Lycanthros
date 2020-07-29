package com.github.jeuxjeux20.loupsgarous.phases.overrides;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class PhaseOverridesModule extends AbstractModule {
    private @Nullable Multibinder<PhaseOverride> phaseOverrideBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigurePhaseOverrides();
    }

    protected void configureBindings() {
    }

    protected void configurePhaseOverrides() {
    }

    private void actualConfigurePhaseOverrides() {
        phaseOverrideBinder = Multibinder.newSetBinder(binder(), PhaseOverride.class);

        configurePhaseOverrides();
    }

    protected final void addPhaseOverride(Class<? extends PhaseOverride> phaseOverride) {
        addPhaseOverride(TypeLiteral.get(phaseOverride));
    }

    protected final void addPhaseOverride(TypeLiteral<? extends PhaseOverride> phaseOverride) {
        Preconditions.checkState(phaseOverrideBinder != null, "addPhaseOverride can only be used inside configurePhaseOverrides()");

        phaseOverrideBinder.addBinding().to(phaseOverride);
    }
}
