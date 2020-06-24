package com.github.jeuxjeux20.loupsgarous.game.inventory;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyOwnerChangeEvent;
import com.google.common.collect.ImmutableList;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EditLobbyItem implements InventoryItem {
    @Override
    public boolean isShown(LGPlayer player, LGGameOrchestrator orchestrator) {
        return orchestrator.lobby().getOwner() == player &&
               !orchestrator.lobby().isLocked();
    }

    @Override
    public ItemStack getItemStack() {
        return ItemStackBuilder.of(Material.EMERALD)
                .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Modifier la composition")
                .build();
    }

    @Override
    public void onClick(LGPlayer player, LGGameOrchestrator orchestrator) {
        orchestrator.lobby().composition().openOwnerGui();
    }

    @Override
    public ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of(LGLobbyOwnerChangeEvent.class, LGGameStartEvent.class);
    }
}
