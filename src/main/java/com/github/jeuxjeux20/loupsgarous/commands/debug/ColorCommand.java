package com.github.jeuxjeux20.loupsgarous.commands.debug;

import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.guicybukkit.command.SelfConfiguredCommandExecutor;
import me.lucko.helper.text.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandName("color")
public class ColorCommand extends SelfConfiguredCommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;
        String sentence = String.join(" ", args);
        sender.sendMessage(Text.colorize(sentence));
        return true;
    }
}
