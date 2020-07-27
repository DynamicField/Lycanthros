package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.guicybukkit.command.SelfConfiguredCommandExecutor;
import com.github.jeuxjeux20.loupsgarous.game.GameCreationException;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.cards.composition.util.DefaultCompositions;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;

@CommandName("lgstart")
public class LGStartCommand extends SelfConfiguredCommandExecutor {
    private final LGGameManager gameManager;

    @Inject
    LGStartCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("loupsgarous.game.start")) {
            sender.sendMessage(error("Vous n'avez pas la permission de lancer une partie. :("));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(error("Impossible de lancer cette command sur la console."));
            return true;
        }

        Player player = ((Player) sender);

        try {
            gameManager.start(player, DefaultCompositions.villagerComposition(8), null);
        } catch (GameCreationException e) {
            sender.sendMessage(ChatColor.RED + "Impossible de créer la partie : " + e.getMessage());
        }
        return true;
    }
}
