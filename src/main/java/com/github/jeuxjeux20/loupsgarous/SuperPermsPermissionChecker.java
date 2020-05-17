package com.github.jeuxjeux20.loupsgarous;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuperPermsPermissionChecker implements PermissionChecker {
    @Override
    public boolean hasPermission(CommandSender sender, String permission) {
        return !(sender instanceof Player) || sender.hasPermission(permission);
    }
}
