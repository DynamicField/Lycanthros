package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.Vote;
import com.github.jeuxjeux20.loupsgarous.interaction.handler.SinglePlayerInteractableCommandHandler;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGVoteCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<Vote<LGPlayer>, SinglePlayerInteractableCommandHandler>> commandBuilderProvider;

    @Inject
    LGVoteCommand(Provider<PickableCommandBuilder<Vote<LGPlayer>, SinglePlayerInteractableCommandHandler>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .failureMessage("Ce n'est pas l'heure de voter !")
                .build(LGInteractableKeys.PLAYER_VOTE)
                .register("lgvote", "lg vote");
    }
}
