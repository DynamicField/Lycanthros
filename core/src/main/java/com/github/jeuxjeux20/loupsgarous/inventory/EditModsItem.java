package com.github.jeuxjeux20.loupsgarous.inventory;

import com.github.jeuxjeux20.loupsgarous.extensibility.gui.ModsGui;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EditModsItem implements InventoryItem {
    @Override
    public boolean isShown(LGPlayer player, LGGameOrchestrator orchestrator) {
        return orchestrator.getOwner() == player && orchestrator.allowsJoin();
    }

    @Override
    public ItemStack getItemStack() {
        return ItemStackBuilder.of(Material.DIAMOND)
                .name(ChatColor.LIGHT_PURPLE + "&lModifier les mods")
                .build();
    }

    @Override
    public void onClick(LGPlayer player, LGGameOrchestrator orchestrator) {
        player.minecraft(mcPlayer -> new ModsGui(mcPlayer, orchestrator).open());
    }
}
