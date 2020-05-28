package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.guicybukkit.command.SelfConfiguredCommandExecutor;
import com.github.jeuxjeux20.loupsgarous.PermissionChecker;
import com.github.jeuxjeux20.loupsgarous.config.LGConfiguration;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.util.DefaultCompositions;
import com.google.inject.Inject;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;

@CommandName("lgstart")
public class LGStartCommand extends SelfConfiguredCommandExecutor {
    private final PermissionChecker permissionChecker;
    private final LGGameManager gameManager;

    @Inject
    public LGStartCommand(PermissionChecker permissionChecker, LGGameManager gameManager) {
        this.permissionChecker = permissionChecker;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!permissionChecker.hasPermission(sender, "loupsgarous.game.start")) {
            sender.sendMessage(error("Vous n'avez pas la permission de lancer une partie. :("));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Impossible de lancer cette command sur la console."));
            return true;
        }
        Player player = ((Player) sender);

        gameManager.startGame(Collections.singleton(player), DefaultCompositions.villagerComposition(8))
                .ifSuccessOrElse(
                        game -> sender.sendMessage(ChatColor.GREEN + "Partie créée !"),
                        error -> sender.sendMessage(ChatColor.RED + "Impossible de créer la partie : " + error)
                );
        return true;
    }
}
