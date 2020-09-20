package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import me.lucko.helper.metadata.MetadataKey;
import org.jetbrains.annotations.Nullable;

public abstract class OrchestratorComponentsModule extends AbstractModule {
    private @Nullable MapBinder<MetadataKey<?>, OrchestratorComponent> orchestratorComponentBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureOrchestratorComponents();
    }

    protected void configureBindings() {
    }

    protected void configureOrchestratorComponents() {
    }

    private void actualConfigureOrchestratorComponents() {
        orchestratorComponentBinder = MapBinder.newMapBinder(binder(),
                new TypeLiteral<MetadataKey<?>>(){},
                TypeLiteral.get(OrchestratorComponent.class));

        configureOrchestratorComponents();
    }

    protected final <T extends OrchestratorComponent>
    void addOrchestratorComponent(MetadataKey<? super T> key,
                                  Class<? extends T> orchestratorComponent) {
        addOrchestratorComponent(key, TypeLiteral.get(orchestratorComponent));
    }

    protected final <T extends OrchestratorComponent>
    void addOrchestratorComponent(MetadataKey<? super T> key,
                                  TypeLiteral<? extends T> orchestratorComponent) {
        Preconditions.checkState(orchestratorComponentBinder != null,
                "addOrchestratorComponent can only be used inside configureOrchestratorComponents()");

        orchestratorComponentBinder.addBinding(key).to(orchestratorComponent);
    }
}
