package com.github.jeuxjeux20.loupsgarous.game.actionbar;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.google.inject.AbstractModule;

public final class LGActionBarModule extends AbstractModule {
    protected void configure() {
        bind(LGActionBarManager.class);

        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.ACTION_BAR,
                        LGActionBarManager.class);
            }
        });
    }
}
