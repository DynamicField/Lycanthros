package com.github.jeuxjeux20.loupsgarous.extensibility.gui;

import com.github.jeuxjeux20.loupsgarous.extensibility.GameBox;
import com.github.jeuxjeux20.loupsgarous.extensibility.Mod;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModDescriptor;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.gui.OwnerGui;
import com.google.common.collect.ImmutableMap;
import io.reactivex.rxjava3.disposables.Disposable;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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

        GameBox gameBox = orchestrator.getGameBox();
        ImmutableMap<Mod, GameBox.ModData> mods = gameBox.getMods();

        for (Map.Entry<Mod, GameBox.ModData> entry : mods.entrySet()) {
            Mod mod = entry.getKey();
            GameBox.ModData modData = entry.getValue();

            ModDescriptor descriptor =
                    orchestrator.getLoupsGarous().getModRegistry().descriptors().get(mod.getClass());

            if (descriptor.isHidden()) {
                continue;
            }

            ItemStack descriptorItem = descriptor.getItem();
            ChatColor statusColor = modData.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
            String statusText = statusColor + (modData.isEnabled() ? "&lactivé" : "&ldesactivé");

            ItemStackBuilder builder = ItemStackBuilder.of(descriptorItem.clone())
                    .hideAttributes()
                    .name(statusColor.toString() + ChatColor.BOLD + descriptor.getName())
                    .lore(ChatColor.WHITE + "Statut : " + statusText)
                    .lore("description? maybe? idk");

            if (modData.isEnabled()) {
                builder.enchant(Enchantment.LUCK, 1);
            } else {
                builder.clearEnchantments();
            }

            Item item = builder.build(() -> gameBox.toggle(mod));
            populator.accept(item);
        }
    }

    private void listenToEvents() {
        bind(Disposable.toAutoCloseable(
                orchestrator.getGameBox().onChange()
                        .subscribe(x -> redraw())
        ));
    }
}
