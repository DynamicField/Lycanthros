package com.github.jeuxjeux20.loupsgarous;

import org.bukkit.command.CommandSender;

/**
 * A basic interface to check if an user got a permission.
 */
public interface PermissionChecker {
    boolean hasPermission(CommandSender sender, String permission);
}
