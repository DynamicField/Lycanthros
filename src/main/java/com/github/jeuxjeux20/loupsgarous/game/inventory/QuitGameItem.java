package com.github.jeuxjeux20.loupsgarous.game.inventory;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameWaitingForPlayersEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageChangingEvent;
import com.google.common.collect.ImmutableList;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class QuitGameItem implements InventoryItem {
    @Override
    public boolean isShown(LGPlayer player, LGGameOrchestrator orchestrator) {
        return true;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemStackBuilder.of(Material.RED_BED)
                .name(ChatColor.RED.toString() + ChatColor.BOLD + "Quitter la partie")
                .build();
    }

    @Override
    public void onClick(LGPlayer player, LGGameOrchestrator orchestrator) {
        orchestrator.lobby().removePlayer(player);
    }

    @Override
    public ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of(LGStageChangingEvent.class, LGGameWaitingForPlayersEvent.class, LGPlayerJoinEvent.class);
    }
}
