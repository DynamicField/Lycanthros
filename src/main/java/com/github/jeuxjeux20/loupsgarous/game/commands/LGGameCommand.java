package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.SelfConfiguredCommandExecutor;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class LGGameCommand extends SelfConfiguredCommandExecutor {
    protected final LGGameManager gameManager;

    @Inject
    public LGGameCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Optional<LGPlayerAndGame> playerAndGame;
        if (!(sender instanceof Player) || !(playerAndGame = gameManager.getPlayerInGame((Player) sender)).isPresent()) {
            sender.sendMessage(ChatColor.RED + "Vous n'êtes pas en partie.");
            return true;
        }
        LGPlayer player = playerAndGame.get().getPlayer();
        Player minecraftPlayer = player.getMinecraftPlayer().orElseThrow(AssertionError::new);
        return run(player, playerAndGame.get().getOrchestrator(), minecraftPlayer, command, label, args);
    }

    protected abstract boolean run(LGPlayer lgPlayer, LGGameOrchestrator orchestrator, Player player, Command command,
                                   String label, String[] args);
}
