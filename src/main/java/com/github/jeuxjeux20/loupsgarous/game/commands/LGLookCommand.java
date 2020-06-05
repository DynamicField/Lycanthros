package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Lookable;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGLookCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<Lookable>> commandBuilderProvider;

    @Inject
    public LGLookCommand(Provider<PickableCommandBuilder<Lookable>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .buildCommand()
                .register("lglook", "lg look");
    }
}
