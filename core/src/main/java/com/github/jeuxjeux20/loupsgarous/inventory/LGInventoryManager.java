package com.github.jeuxjeux20.loupsgarous.inventory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.event.registry.RegistryChangeEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.registry.GameRegistries;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponent;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.protocol.Protocol;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LGInventoryManager extends OrchestratorComponent {
    private static final MetadataKey<Map<Integer, InventoryItem>> ITEMS_KEY
            = MetadataKey.create("inv_manager", new TypeToken<Map<Integer, InventoryItem>>() {});

    public LGInventoryManager(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        registerEvents();
    }

    private void registerEvents() {
        registerUpdateItemsEvent();

        // TODO: Fix this so it works with a left click on a block as well.
        Events.subscribe(PlayerInteractEvent.class)
                .filter(this::concernsMe)
                .filter(e -> e.getAction() != Action.PHYSICAL)
                .filter(PlayerInteractEvent::hasItem)
                .handler(e -> handleItemClick(e.getPlayer(),
                        e.getPlayer().getInventory().getHeldItemSlot()))
                .bindWith(this);

        Events.subscribe(InventoryClickEvent.class)
                .filter(this::concernsMe)
                .handler(this::handleInventoryClick)
                .bindWith(this);

        Events.subscribe(InventoryDragEvent.class)
                .filter(this::concernsMe)
                .handler(this::cancelEvent)
                .bindWith(this);

        Events.subscribe(PlayerDropItemEvent.class)
                .filter(this::concernsMe)
                .handler(this::cancelEvent)
                .bindWith(this);

        Events.subscribe(LGPlayerQuitEvent.class)
                .filter(this::concernsMe)
                .handler(e -> clearPlayerInventory(e.getPlayerUUID()))
                .bindWith(this);

        Protocol.subscribe(PacketType.Play.Server.ENTITY_EQUIPMENT)
                .filter(this::concernsMe)
                .handler(this::hideHeldItem)
                .bindWith(this);
    }

    private void registerUpdateItemsEvent() {
        // TODO: fix this

        Schedulers.sync().runRepeating(this::updateAllInventories, 0L, 10L)
                .bindWith(this);

        Events.subscribe(RegistryChangeEvent.class)
                .filter(e -> e.getRegistry() == GameRegistries.INVENTORY_ITEMS.get(orchestrator))
                .handler(e -> updateAllInventories())
                .bindWith(this);
    }

    public void update(LGPlayer player) {
        Player minecraftPlayer = player.minecraft().orElse(null);
        if (minecraftPlayer == null) { return; }

        PlayerInventory inventory = minecraftPlayer.getInventory();
        inventory.clear();

        Map<Integer, InventoryItem> itemSlots =
                Metadata.provideForPlayer(minecraftPlayer).getOrPut(ITEMS_KEY, HashMap::new);
        List<InventoryItem> inventoryItems = getInventoryItems();

        int slot = 8;
        for (int i = inventoryItems.size() - 1; i >= 0; i--) {
            InventoryItem item = inventoryItems.get(i);

            if (!item.isShown(player, orchestrator)) { return; }

            inventory.setItem(slot, item.getItemStack());

            itemSlots.put(slot, item);
            slot--;
            if (slot == 0) {
                slot = 36;
            }
        }
    }

    private void handleInventoryClick(InventoryClickEvent event) {
        if (event.getView().getBottomInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        event.setCancelled(true);

        handleItemClick(event.getWhoClicked(), event.getSlot());
    }

    private void handleItemClick(HumanEntity player, int slot) {
        LGPlayer lgPlayer = orchestrator.getPlayerOrThrow(player.getUniqueId());

        InventoryItem item = getItem(lgPlayer, slot);
        if (item != null) {
            item.onClick(lgPlayer, orchestrator);
        }
    }

    private @Nullable InventoryItem getItem(LGPlayer player, int slot) {
        Map<Integer, InventoryItem> map =
                Metadata.provideForPlayer(player.getPlayerUUID()).getOrNull(ITEMS_KEY);

        return map == null ? null : map.get(slot);
    }

    private void cancelEvent(Cancellable event) {
        event.setCancelled(true);
    }

    private void clearPlayerInventory(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.getInventory().clear();
            Metadata.provideForPlayer(player).remove(ITEMS_KEY);
        }
    }

    private void hideHeldItem(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        if (packet.getItemSlots().read(0) == EnumWrappers.ItemSlot.MAINHAND) {
            packet.getItemModifier().write(0, new ItemStack(Material.AIR));
        }
    }

    private void updateAllInventories() {
        for (LGPlayer player : orchestrator.getPlayers()) {
            update(player);
        }
    }


    private boolean concernsMe(PlayerEvent event) {
        return isPlayerInGame(event.getPlayer());
    }

    private boolean concernsMe(PacketEvent event) {
        return isPlayerInGame(event.getPlayer());
    }

    private boolean concernsMe(InventoryInteractEvent event) {
        return isPlayerInGame(event.getWhoClicked());
    }

    private boolean concernsMe(LGEvent event) {
        return orchestrator.isMyEvent(event);
    }

    private boolean isPlayerInGame(HumanEntity player) {
        return orchestrator.getPlayer(player.getUniqueId()).isPresent();
    }

    private ImmutableList<InventoryItem> getInventoryItems() {
        return GameRegistries.INVENTORY_ITEMS.get(orchestrator).getValues().asList();
    }
}
