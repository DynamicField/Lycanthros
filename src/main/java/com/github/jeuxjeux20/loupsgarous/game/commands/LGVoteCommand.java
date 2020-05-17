package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

@CommandName("lgvote")
public class LGVoteCommand implements AnnotatedCommandConfigurator {
    private final Provider<PickableCommandBuilder<Votable>> commandBuilderProvider;

    @Inject
    public LGVoteCommand(Provider<PickableCommandBuilder<Votable>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        commandBuilderProvider.get()
                .withCannotPickErrorMessage("Ce n'est pas l'heure de voter !")
                .buildCommand()
                .register(getCommandName());
    }
}
