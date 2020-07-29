package com.github.jeuxjeux20.loupsgarous.phases.overrides;

import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;
import com.google.inject.TypeLiteral;

public abstract class AbstractPhaseOverride<T extends RunnableLGPhase> implements PhaseOverride {
    private final RunnableLGPhase.Factory<T> phaseFactory;
    private final TypeLiteral<T> phaseTypeLiteral;

    protected AbstractPhaseOverride(RunnableLGPhase.Factory<T> factory, TypeLiteral<T> typeLiteral) {
        this.phaseFactory = factory;
        this.phaseTypeLiteral = typeLiteral;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends T> getPhaseClass() {
        // Since we just need the raw type for instanceof, this is safe.
        return (Class<? extends T>) phaseTypeLiteral.getRawType();
    }

    @Override
    public RunnableLGPhase.Factory<T> getPhaseFactory() {
        return phaseFactory;
    }
}
