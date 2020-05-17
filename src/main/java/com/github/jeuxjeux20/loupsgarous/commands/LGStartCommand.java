package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.guicybukkit.command.SelfConfiguredCommandExecutor;
import com.github.jeuxjeux20.loupsgarous.PermissionChecker;
import com.github.jeuxjeux20.loupsgarous.config.LGConfiguration;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.google.inject.Inject;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@CommandName("lgstart")
public class LGStartCommand extends SelfConfiguredCommandExecutor {
    private final PermissionChecker permissionChecker;
    private final LGGameManager LGGameManager;
    private final LGConfiguration configuration;
    private final MultiverseCore multiverse;

    @Inject
    public LGStartCommand(PermissionChecker permissionChecker,
                          LGGameManager LGGameManager,
                          LGConfiguration configuration,
                          MultiverseCore multiverse) {
        this.permissionChecker = permissionChecker;
        this.LGGameManager = LGGameManager;
        this.configuration = configuration;
        this.multiverse = multiverse;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;
        if (!permissionChecker.hasPermission(sender, "loups.garous.game.start")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de lancer une partie. :(");
            return true;
        }
        Player[] players = new Player[args.length];
        for (int i = 0; i < args.length; i++) {
            String playerName = args[i];
            Player player = sender.getServer().getPlayer(playerName);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Impossible de trouver le joueur " + playerName + ".");
                return true;
            }
            players[i] = player;
        }
        String world = configuration.getDefaultWorld();
        if (!multiverse.getMVWorldManager().isMVWorld(world)) {
            sender.sendMessage(ChatColor.RED + "Le monde" + world + " n'existe pas.");
        } else {
            LGGameManager.startGame(world, Arrays.asList(players), sender)
                    .ifSuccessOrElse(
                            game -> sender.sendMessage(ChatColor.GREEN + "Partie créée !"),
                            error -> sender.sendMessage(ChatColor.RED + "Impossible de créer la partie : " + error)
                    );
        }
        return true;
    }
}
