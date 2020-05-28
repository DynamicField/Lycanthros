package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Killable;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

@CommandName("lgkill")
public class LGKillCommand implements AnnotatedCommandConfigurator {
    private final Provider<PickableCommandBuilder<Killable>> commandBuilderProvider;

    @Inject
    LGKillCommand(Provider<PickableCommandBuilder<Killable>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        commandBuilderProvider.get()
                .buildCommand()
                .register(getCommandName());
    }
}
