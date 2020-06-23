package com.github.jeuxjeux20.loupsgarous.game.lobby;

import org.bukkit.entity.Player;

public class PlayerOfflineException extends PlayerJoinException {
    public PlayerOfflineException() {
    }

    public PlayerOfflineException(Player player) {
        this("The player '" + player + "' is offline.");
    }

    public PlayerOfflineException(String message) {
        super(message);
    }

    public PlayerOfflineException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerOfflineException(Throwable cause) {
        super(cause);
    }
}
