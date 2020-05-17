package com.github.df.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.CommandConfigurator;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.Nullable;

public interface HelperCommandRegisterer extends CommandConfigurator {
    void register();

    @Override
    @Deprecated
    default @Nullable String getCommandName() {
        return null;
    }

    @Override
    default void configureCommand(@Nullable PluginCommand command) {
        register();
    }
}
