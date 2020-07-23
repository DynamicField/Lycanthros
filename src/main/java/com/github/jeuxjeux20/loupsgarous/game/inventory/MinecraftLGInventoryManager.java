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
import me.lucko.helper.Events;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@OrchestratorScoped
public class MinecraftLGInventoryManager
        extends AbstractOrchestratorComponent
        implements LGInventoryManager {
    private static final MetadataKey<Map<Integer, InventoryItem>> ITEMS_KEY
            = MetadataKey.create("inv_manager", new TypeToken<Map<Integer, InventoryItem>>() {});
    private final Set<InventoryItem> inventoryItems;

    @Inject
    MinecraftLGInventoryManager(LGGameOrchestrator orchestrator,
                                Set<InventoryItem> inventoryItems) {
        super(orchestrator);
        this.inventoryItems = inventoryItems;

        registerEvents();
    }

    private void registerEvents() {
        registerUpdateItemsEvent();

        Events.subscribe(PlayerInteractEvent.class)
                .filter(this::concernsMe)
                .filter(e -> e.getAction() != Action.PHYSICAL)
                .handler(e -> handleItemClick(e.getPlayer(), e.getPlayer().getInventory().getHeldItemSlot()))
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
        Events.merge(LGEvent.class,
                ClassArrayUtils.merge(inventoryItems.stream().map(HasTriggers::getUpdateTriggers)))
                .filter(this::concernsMe)
                .handler(this::updateAllInventories);
    }

    @Override
    public void update(LGPlayer player) {
        Player minecraftPlayer = player.getMinecraftPlayer().orElse(null);
        if (minecraftPlayer == null) return;

        PlayerInventory inventory = minecraftPlayer.getInventory();

        inventory.clear();

        Map<Integer, InventoryItem> itemSlots =
                Metadata.provideForPlayer(minecraftPlayer).getOrPut(ITEMS_KEY, HashMap::new);
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

    private void handleInventoryClick(InventoryClickEvent event) {
        if (event.getView().getBottomInventory().getType() != InventoryType.PLAYER) {
            return;
        }

        event.setCancelled(true);

        handleItemClick(event.getWhoClicked(), event.getSlot());
    }

    private void handleItemClick(HumanEntity player, int slot) {
        LGPlayer lgPlayer = orchestrator.game().getPlayerOrThrow(player.getUniqueId());

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

    private void updateAllInventories(LGEvent e) {
        for (LGPlayer player : e.getOrchestrator().game().getPlayers()) {
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
        return orchestrator.game().getPlayer(player.getUniqueId()).isPresent();
    }
}
