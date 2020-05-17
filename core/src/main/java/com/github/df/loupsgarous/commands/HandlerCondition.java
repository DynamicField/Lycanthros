package com.github.df.loupsgarous.commands;

import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.command.CommandSender;

public interface HandlerCondition<S extends CommandSender, T> {
    FunctionalCommandHandler<S> wrap(T handler);
}
