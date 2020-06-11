package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.config.LGConfiguration;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.ChatColor;

public class LGReloadConfigCommand implements HelperCommandRegisterer {
    private final LGConfiguration configuration;

    @Inject
    LGReloadConfigCommand(LGConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void register() {
        Commands.create()
                .assertOp("Vous devez être op pour exécuter cette commande.")
                .handler(c -> {
                    configuration.reload();
                    c.reply(ChatColor.GREEN + "Configuration rechargée !");
                })
                .register("lgreloadconfig", "lg reloadconfig");
    }
}
