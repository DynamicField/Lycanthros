package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Healable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGHealCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<Healable, Pickable<LGPlayer>>> commandBuilderProvider;

    @Inject
    LGHealCommand(Provider<PickableCommandBuilder<Healable, Pickable<LGPlayer>>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .buildCommand()
                .register("lgheal", "lg heal");
    }
}
