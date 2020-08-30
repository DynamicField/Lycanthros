package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhasesDescriptorModule;
import com.github.jeuxjeux20.loupsgarous.phases.listeners.LGPhasesListenersModule;
import com.google.inject.AbstractModule;

public final class LGPhasesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGPhasesOrchestrator.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.PHASES, LGPhasesOrchestrator.class);
            }
        });

        install(new LGPhasesListenersModule());
        install(new LGPhasesDescriptorModule());
    }
}
