package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PlayerVotable;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGVoteCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<PlayerVotable, Pickable<LGPlayer>>> commandBuilderProvider;

    @Inject
    LGVoteCommand(Provider<PickableCommandBuilder<PlayerVotable, Pickable<LGPlayer>>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .withCannotPickErrorMessage("Ce n'est pas l'heure de voter !")
                .buildCommand()
                .register("lgvote", "lg vote");
    }
}
