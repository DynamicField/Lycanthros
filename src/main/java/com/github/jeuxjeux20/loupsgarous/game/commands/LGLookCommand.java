package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Lookable;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

@CommandName("lglook")
public class LGLookCommand implements AnnotatedCommandConfigurator {
    private final Provider<PickableCommandBuilder<Lookable>> commandBuilderProvider;

    @Inject
    public LGLookCommand(Provider<PickableCommandBuilder<Lookable>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        commandBuilderProvider.get()
                .buildCommand()
                .register(getCommandName());
    }
}
