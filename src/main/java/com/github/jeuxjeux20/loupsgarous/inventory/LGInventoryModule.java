package com.github.jeuxjeux20.loupsgarous.inventory;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;

public final class LGInventoryModule extends InventoryItemsModule {
    @Override
    protected void configureBindings() {
        bind(LGInventoryManager.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.INVENTORY, LGInventoryManager.class);
            }
        });
    }

    @Override
    protected void configureInventoryItems() {
        addInventoryItem(QuitGameItem.class);
        addInventoryItem(EditLobbyItem.class);
    }
}
