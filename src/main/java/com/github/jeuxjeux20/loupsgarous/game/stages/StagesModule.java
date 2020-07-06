package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;

public abstract class StagesModule extends AbstractModule {
    private Multibinder<RunnableLGStage> stagesBinder;
    private Multibinder<RunnableLGStage.Factory<?>> stageFactoriesBinder;

    private boolean canConfigureStages;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureStages();
    }

    protected void configureBindings() {
    }

    protected void configureStages() {
    }

    private void actualConfigureStages() {
        stagesBinder = Multibinder.newSetBinder(binder(), RunnableLGStage.class);
        stageFactoriesBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<RunnableLGStage.Factory<?>>() {});
        canConfigureStages = true;

        configureStages();
    }

    protected final void addStage(Class<? extends RunnableLGStage> stage) {
        addStage(TypeLiteral.get(stage));
    }

    protected final <T extends RunnableLGStage> void addStage(TypeLiteral<T> stage) {
        Preconditions.checkState(canConfigureStages, "addStage can only be used inside configureStages()");

        stagesBinder.addBinding().to(stage).in(OrchestratorScoped.class);
        bind(stage).in(OrchestratorScoped.class);

        registerStageFactory(stage);
    }

    protected final <T extends RunnableLGStage> void registerStageFactory(Class<T> stage) {
        registerStageFactory(TypeLiteral.get(stage));
    }

    protected final <T extends RunnableLGStage> void registerStageFactory(TypeLiteral<T> stage) {
        ProviderRunnableStageFactory<T> factory = new ProviderRunnableStageFactory<>(getProvider(Key.get(stage)));

        bind(createFactoryType(stage)).toInstance(factory);
        stageFactoriesBinder.addBinding().toInstance(factory);
    }

    @SuppressWarnings("unchecked")
    private <T extends RunnableLGStage>
    TypeLiteral<RunnableLGStage.Factory<T>> createFactoryType(TypeLiteral<T> type) {
        return (TypeLiteral<RunnableLGStage.Factory<T>>) TypeLiteral.get(
                Types.newParameterizedTypeWithOwner(
                        RunnableLGStage.class,
                        RunnableLGStage.Factory.class,
                        type.getType()
                )
        );
    }
}
