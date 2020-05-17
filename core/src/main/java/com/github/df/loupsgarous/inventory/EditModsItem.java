package com.github.df.loupsgarous.inventory;

import com.github.df.loupsgarous.extensibility.gui.ModsGui;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EditModsItem extends InventoryItem {
    public EditModsItem(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public boolean isShown(LGPlayer player) {
        return orchestrator.getOwner() == player && orchestrator.allowsJoin();
    }

    @Override
    public ItemStack render() {
        return ItemStackBuilder.of(Material.DIAMOND)
                .name(ChatColor.LIGHT_PURPLE + "&lModifier les mods")
                .build();
    }

    @Override
    public void onClick(LGPlayer player) {
        player.minecraft(mcPlayer -> new ModsGui(mcPlayer, orchestrator).open());
    }
}
