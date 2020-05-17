package com.github.df.loupsgarous.inventory;

import com.github.df.loupsgarous.cards.composition.CompositionGui;
import com.github.df.loupsgarous.event.LGEvent;
import com.github.df.loupsgarous.event.LGGameStartEvent;
import com.github.df.loupsgarous.event.lobby.LGOwnerChangeEvent;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.google.common.collect.ImmutableList;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EditLobbyItem extends InventoryItem {
    public EditLobbyItem(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public boolean isShown(LGPlayer player) {
        return orchestrator.getOwner() == player && orchestrator.allowsJoin();
    }

    @Override
    public ItemStack render() {
        return ItemStackBuilder.of(Material.EMERALD)
                .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Modifier la composition")
                .build();
    }

    @Override
    public void onClick(LGPlayer player) {
        player.minecraft(mcPlayer -> new CompositionGui(mcPlayer, orchestrator).open());
    }

    @Override
    public ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of(LGOwnerChangeEvent.class, LGGameStartEvent.class);
    }
}
