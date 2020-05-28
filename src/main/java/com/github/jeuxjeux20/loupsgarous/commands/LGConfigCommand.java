package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.guicybukkit.command.SelfConfiguredCommandExecutor;
import com.github.jeuxjeux20.loupsgarous.config.LGConfiguration;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandName("lgconfig")
public class LGConfigCommand extends SelfConfiguredCommandExecutor {
    private final LGConfiguration configuration;

    @Inject
    LGConfigCommand(LGConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("loupsgarous.config.edit")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de modifier la configuration. :(");
        }
        if (args.length <= 2) return false;
        if (args[0].equals("set")) {
            if (args[1].equals("default-world")) {
                configuration.setDefaultWorld(args[2]);
            } else {
                sender.sendMessage(ChatColor.RED + "Paramètre inconnu : " + args[1]);
            }
        } else {
            return false;
        }
        return true;
    }
}
