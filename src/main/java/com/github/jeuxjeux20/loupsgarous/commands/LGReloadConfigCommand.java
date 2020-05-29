package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

@CommandName("lgreloadconfig")
public class LGReloadConfigCommand implements AnnotatedCommandConfigurator {
    private final LoupsGarous plugin;

    @Inject
    LGReloadConfigCommand(LoupsGarous plugin) {
        this.plugin = plugin;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        Commands.create()
                .assertOp("Vous devez être op pour exécuter cette commande.")
                .handler(c -> {
                    plugin.reloadConfig();
                    c.reply(ChatColor.GREEN + "Configuration rechargée !");
                })
                .register(getCommandName());
    }
}
