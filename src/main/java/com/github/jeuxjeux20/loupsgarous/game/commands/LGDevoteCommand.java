package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PlayerVotable;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

@CommandName("lgdevote")
public class LGDevoteCommand extends LGGameCommand {
    @Inject
    LGDevoteCommand(LGGameManager gameManager) {
        super(gameManager);
    }

    @Override
    protected boolean run(LGPlayer lgPlayer, LGGameOrchestrator orchestrator,
                          Player player, Command command, String label, String[] args) {
        if (args.length != 0) return false;

        LGStage stage = orchestrator.stages().current();

        PlayerVotable votable = stage.getComponent(PlayerVotable.class).orElse(null);
        if (votable == null) {
            player.sendMessage(ChatColor.RED + "Ce n'est pas l'heure de voter !");
            return true;
        }

        if (!votable.hasPick(lgPlayer)) {
            player.sendMessage(ChatColor.RED + "Vous ne votez pour personne.");
        } else {
            votable.removePick(lgPlayer);
        }
        return true;
    }
}
