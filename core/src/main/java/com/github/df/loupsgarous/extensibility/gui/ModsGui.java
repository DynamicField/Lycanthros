package com.github.df.loupsgarous.extensibility.gui;

import com.github.df.loupsgarous.event.extensibility.*;
import com.github.df.loupsgarous.event.extensibility.*;
import com.github.df.loupsgarous.extensibility.Mod;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.gui.OwnerGui;
import me.lucko.helper.Events;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ModsGui extends OwnerGui {
    private static final MenuScheme MODS = new MenuScheme()
            .mask("000000000")
            .mask("011111110")
            .mask("011111110")
            .mask("011111110")
            .mask("011111110")
            .mask("000000000");

    public ModsGui(Player player, LGGameOrchestrator orchestrator) {
        super(player, 6, "Mods", orchestrator);
    }

    @Override
    public void open() {
        super.open();

        listenToEvents();
    }

    @Override
    public void redraw() {
        MenuPopulator populator = MODS.newPopulator(this);

        for (Mod mod : orchestrator.getModsContainer().getMods()) {
            if (mod.getDescriptor().isHidden()) {
                continue;
            }

            ItemStack descriptorItem = mod.getDescriptor().getItem();
            ChatColor statusColor = mod.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
            String statusText = statusColor + (mod.isEnabled() ? "&lactivé" : "&ldesactivé");

            ItemStackBuilder builder = ItemStackBuilder.of(descriptorItem.clone())
                    .hideAttributes()
                    .name(statusColor.toString() + ChatColor.BOLD + mod.getDescriptor().getName())
                    .lore(ChatColor.WHITE + "Statut : " + statusText)
                    .lore("description? maybe? idk");

            if (mod.isEnabled()) {
                builder.enchant(Enchantment.LUCK, 1);
            } else {
                builder.clearEnchantments();
            }

            Item item = builder.build(() -> orchestrator.getModsContainer().toggle(mod));
            populator.accept(item);
        }
    }

    private void listenToEvents() {
        Events.merge(ModEvent.class,
                ModAddedEvent.class, ModRemovedEvent.class,
                ModEnabledEvent.class, ModDisabledEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> redraw())
                .bindWith(this);
    }
}
