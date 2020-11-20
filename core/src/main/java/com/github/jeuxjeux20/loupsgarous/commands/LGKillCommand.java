package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.Pick;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.handler.SinglePlayerInteractableCommandHandler;
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
