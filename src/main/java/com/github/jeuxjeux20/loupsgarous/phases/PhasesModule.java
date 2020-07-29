package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;

public abstract class PhasesModule extends AbstractModule {
    private Multibinder<RunnableLGPhase> phasesBinder;
    private Multibinder<RunnableLGPhase.Factory<?>> phaseFactoriesBinder;

    private boolean canConfigurePhases;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigurePhases();
    }

    protected void configureBindings() {
    }

    protected void configurePhases() {
    }

    private void actualConfigurePhases() {
        phasesBinder = Multibinder.newSetBinder(binder(), RunnableLGPhase.class);
        phaseFactoriesBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<RunnableLGPhase.Factory<?>>() {});
        canConfigurePhases = true;

        configurePhases();
    }

    protected final void addPhase(Class<? extends RunnableLGPhase> phase) {
        addPhase(TypeLiteral.get(phase));
    }

    protected final <T extends RunnableLGPhase> void addPhase(TypeLiteral<T> phase) {
        Preconditions.checkState(canConfigurePhases, "addPhase can only be used inside configurePhases()");

        phasesBinder.addBinding().to(phase).in(OrchestratorScoped.class);
        bind(phase).in(OrchestratorScoped.class);

        registerPhaseFactory(phase, true);
    }

    private <T extends RunnableLGPhase> void registerPhaseFactory(TypeLiteral<T> phase, boolean addInMultibinder) {
        ProviderRunnablePhaseFactory<T> factory = new ProviderRunnablePhaseFactory<>(getProvider(Key.get(phase)));

        bind(createFactoryType(phase)).toInstance(factory);

        if (addInMultibinder) {
            phaseFactoriesBinder.addBinding().toInstance(factory);
        }
    }

    protected final <T extends RunnableLGPhase> void registerPhaseFactory(Class<T> phase) {
        registerPhaseFactory(TypeLiteral.get(phase));
    }

    protected final <T extends RunnableLGPhase> void registerPhaseFactory(TypeLiteral<T> phase) {
        registerPhaseFactory(phase, false);
    }

    @SuppressWarnings("unchecked")
    private <T extends RunnableLGPhase>
    TypeLiteral<RunnableLGPhase.Factory<T>> createFactoryType(TypeLiteral<T> type) {
        return (TypeLiteral<RunnableLGPhase.Factory<T>>) TypeLiteral.get(
                Types.newParameterizedTypeWithOwner(
                        RunnableLGPhase.class,
                        RunnableLGPhase.Factory.class,
                        type.getType()
                )
        );
    }
}
