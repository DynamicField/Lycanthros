package com.github.df.loupsgarous.commands;

import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.Pick;
import com.github.df.loupsgarous.interaction.LGInteractableKeys;
import com.github.df.loupsgarous.interaction.handler.SinglePlayerInteractableCommandHandler;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGKillCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<Pick<LGPlayer>, SinglePlayerInteractableCommandHandler>> commandBuilderProvider;

    @Inject
    LGKillCommand(Provider<PickableCommandBuilder<Pick<LGPlayer>, SinglePlayerInteractableCommandHandler>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .build(LGInteractableKeys.KILL)
                .register("lgkill", "lg kill");
    }
}
