package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Killable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGKillCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<Killable, Pickable<LGPlayer>>> commandBuilderProvider;

    @Inject
    LGKillCommand(Provider<PickableCommandBuilder<Killable, Pickable<LGPlayer>>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .buildCommand()
                .register("lgkill", "lg kill");
    }
}
