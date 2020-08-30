package com.github.jeuxjeux20.loupsgarous.inventory;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.google.inject.AbstractModule;

public final class LGInventoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGInventoryManager.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.INVENTORY, LGInventoryManager.class);
            }
        });
    }
}
