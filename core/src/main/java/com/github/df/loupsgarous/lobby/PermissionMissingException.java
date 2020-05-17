package com.github.df.loupsgarous.lobby;

import org.bukkit.entity.Player;

public class PermissionMissingException extends PlayerJoinException {
    public PermissionMissingException() {
    }

    public PermissionMissingException(String permission, Player player) {
        super("The permission '" + permission + "' is missing on player '" + player + "'.");
    }

    public PermissionMissingException(String message) {
        super(message);
    }

    public PermissionMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionMissingException(Throwable cause) {
        super(cause);
    }
}
