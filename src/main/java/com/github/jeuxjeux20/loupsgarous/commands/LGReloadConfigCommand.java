package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.ChatColor;

public class LGReloadConfigCommand implements HelperCommandRegisterer {
    private final LoupsGarous plugin;

    @Inject
    LGReloadConfigCommand(LoupsGarous plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        Commands.create()
                .assertOp("Vous devez être op pour exécuter cette commande.")
                .handler(c -> {
                    plugin.reloadConfig();
                    c.reply(ChatColor.GREEN + "Configuration rechargée !");
                })
                .register("lgreloadconfig", "lg reloadconfig");
    }
}
