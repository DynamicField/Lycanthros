package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Healable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PlayerPickable;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGHealCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<Healable, PlayerPickable>> commandBuilderProvider;

    @Inject
    LGHealCommand(Provider<PickableCommandBuilder<Healable, PlayerPickable>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .buildCommand()
                .register("lgheal", "lg heal");
    }
}
