package com.github.jeuxjeux20.loupsgarous.game.inventory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.util.ClassArrayUtils;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.Events;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.protocol.Protocol;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

@Singleton
public class MinecraftLGInventoryManager implements LGInventoryManager {
    private final Set<InventoryItem> inventoryItems;
    private final LGGameManager gameManager;
    private final MetadataKey<Map<Integer, InventoryItem>> itemsKey
            = MetadataKey.create("inv_manager", new TypeToken<Map<Integer, InventoryItem>>() {});

    private boolean hasEvents;

    @Inject
    MinecraftLGInventoryManager(Set<InventoryItem> inventoryItems, LGGameManager gameManager) {
        this.inventoryItems = inventoryItems;
        this.gameManager = gameManager;

        registerEvents();
    }

    private void registerEvents() {
        if (hasEvents) return;
        hasEvents = true;

        registerUpdateItemsEvent();

        Events.subscribe(PlayerInteractEvent.class)
                .filter(e -> e.getAction() != Action.PHYSICAL)
                .handler(e -> handleItemClick(e.getPlayer(), e.getPlayer().getInventory().getHeldItemSlot()));

        Events.subscribe(InventoryClickEvent.class)
                .handler(this::handleInventoryClick);

        Events.subscribe(InventoryDragEvent.class)
                .handler(e -> cancelEvent(e.getWhoClicked().getUniqueId(), e));

        Events.subscribe(PlayerDropItemEvent.class)
                .handler(e -> cancelEvent(e.getPlayer().getUniqueId(), e));

        Events.subscribe(LGPlayerQuitEvent.class)
                .handler(this::clearPlayerInventory);

        Protocol.subscribe(PacketType.Play.Server.ENTITY_EQUIPMENT)
                .handler(this::hideHeldItem);
    }

    private void registerUpdateItemsEvent() {
        Events.merge(LGEvent.class, ClassArrayUtils.merge(inventoryItems.stream().map(HasTriggers::getUpdateTriggers)))
                .handler(e -> {
                    for (LGPlayer player : e.getOrchestrator().game().getPlayers()) {
                        update(player, e.getOrchestrator());
                    }
                });
    }

    @Override
    public void update(LGPlayer player, LGGameOrchestrator orchestrator) {
        Player minecraftPlayer = player.getMinecraftPlayer().orElse(null);
        if (minecraftPlayer == null) return;

        PlayerInventory inventory = minecraftPlayer.getInventory();

        inventory.clear();

        Map<Integer, InventoryItem> itemSlots = Metadata.provideForPlayer(minecraftPlayer).getOrPut(itemsKey, HashMap::new);
        int slot = 8;
        for (InventoryItem item : inventoryItems) {
            if (!item.isShown(player, orchestrator)) return;

            inventory.setItem(slot, item.getItemStack());

            itemSlots.put(slot, item);
            slot--;
            if (slot == 0) {
                slot = 36;
            }
        }
    }

    private Optional<Map<Integer, InventoryItem>> getMap(LGPlayerAndGame playerAndGame) {
        return Metadata.provideForPlayer(playerAndGame.getPlayer().getPlayerUUID()).get(itemsKey);
    }

    private Optional<InventoryItem> getAt(LGPlayerAndGame playerAndGame, int slot) {
        return getMap(playerAndGame).map(x -> x.get(slot));
    }

    private void handleItemClick(HumanEntity player, int slot) {
        UUID playerUUID = player.getUniqueId();

        Optional<LGPlayerAndGame> playerInGame = gameManager.getPlayerInGame(playerUUID);

        playerInGame.flatMap(x -> getAt(x, slot))
                .ifPresent(inventory -> inventory.onClick(playerInGame.get()));
    }

    private void cancelEvent(UUID uuid, Cancellable e) {
        gameManager.getPlayerInGame(uuid)
                .ifPresent(pg -> e.setCancelled(true));
    }

    private void clearPlayerInventory(LGPlayerQuitEvent e) {
        Player player = e.getLGPlayer().getOfflineMinecraftPlayer().getPlayer();
        if (player != null) {
            player.getInventory().clear();
        }

        Metadata.provideForPlayer(e.getLGPlayer().getPlayerUUID()).remove(itemsKey);
    }

    private void hideHeldItem(PacketEvent e) {
        Player player = e.getPlayer();
        PacketContainer packet = e.getPacket();

        if (!gameManager.getPlayerInGame(player).isPresent()) {
            return;
        }

        if (packet.getItemSlots().read(0) == EnumWrappers.ItemSlot.MAINHAND) {
            packet.getItemModifier().write(0, new ItemStack(Material.AIR));
        }
    }

    private void handleInventoryClick(InventoryClickEvent e) {
        if (!gameManager.getPlayerInGame(e.getWhoClicked().getUniqueId()).isPresent() ||
            e.getView().getBottomInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        e.setCancelled(true);

        handleItemClick(e.getWhoClicked(), e.getSlot());
    }
}
