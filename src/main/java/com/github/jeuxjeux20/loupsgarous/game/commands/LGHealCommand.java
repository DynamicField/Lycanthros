package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Healable;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

@CommandName("lgheal")
public class LGHealCommand implements AnnotatedCommandConfigurator {
    private final Provider<PickableCommandBuilder<Healable>> commandBuilderProvider;

    @Inject
    LGHealCommand(Provider<PickableCommandBuilder<Healable>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        commandBuilderProvider.get()
                .buildCommand()
                .register(getCommandName());
    }
}
