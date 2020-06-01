package com.github.jeuxjeux20.loupsgarous.game.inventory;

public final class LGInventoryModule extends InventoryItemsModule {
    @Override
    protected void configureBindings() {
        bind(LGInventoryManager.class).to(MinecraftLGInventoryManager.class);
    }

    @Override
    protected void configureInventoryItems() {
        addInventoryItem(QuitGameItem.class);
        addInventoryItem(EditLobbyItem.class);
    }
}
