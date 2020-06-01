package com.github.jeuxjeux20.loupsgarous.game.inventory;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryItemsModule extends AbstractModule {
    private @Nullable Multibinder<InventoryItem> inventoryItemBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureInventoryItems();
    }

    protected void configureBindings() {
    }

    protected void configureInventoryItems() {
    }

    private void actualConfigureInventoryItems() {
        inventoryItemBinder = Multibinder.newSetBinder(binder(), InventoryItem.class);

        configureInventoryItems();
    }

    protected final void addInventoryItem(Class<? extends InventoryItem> inventoryItem) {
        addInventoryItem(TypeLiteral.get(inventoryItem));
    }

    protected final void addInventoryItem(TypeLiteral<? extends InventoryItem> inventoryItem) {
        Preconditions.checkState(inventoryItemBinder != null, "addInventoryItem can only be used inside configureInventoryItems()");

        inventoryItemBinder.addBinding().to(inventoryItem);
    }
}
