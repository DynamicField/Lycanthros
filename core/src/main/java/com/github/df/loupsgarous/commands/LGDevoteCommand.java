package com.github.df.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.df.loupsgarous.SafeResult;
import com.github.df.loupsgarous.game.LGGameManager;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.LGInteractableKeys;
import com.github.df.loupsgarous.interaction.vote.Vote;
import com.google.common.reflect.TypeToken;
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

        SafeResult<Vote<LGPlayer>> maybeVotable = orchestrator.interactables()
                .single(LGInteractableKeys.PLAYER_VOTE)
                .type(new TypeToken<Vote<LGPlayer>>() {})
                .check(x -> x.conditions().checkPicker(lgPlayer))
                .failureMessage("Ce n'est pas l'heure de voter !")
                .get();

        maybeVotable.ifSuccessOrElse(
                votable -> {
                    if (!votable.hasPick(lgPlayer)) {
                        player.sendMessage(ChatColor.RED + "Vous ne votez pour personne.");
                    } else {
                        votable.removePick(lgPlayer);
                    }
                },
                error -> player.sendMessage(ChatColor.RED + error)
        );

        return true;
    }
}

