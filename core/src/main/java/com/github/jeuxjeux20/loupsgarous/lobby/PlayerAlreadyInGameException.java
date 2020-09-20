package com.github.jeuxjeux20.loupsgarous.lobby;

import org.bukkit.entity.Player;

public class PlayerAlreadyInGameException extends PlayerJoinException {
    public PlayerAlreadyInGameException() {
    }

    public PlayerAlreadyInGameException(Player player) {
        this("The player '" + player.getName() + "' is already in a game.");
    }

    public PlayerAlreadyInGameException(String message) {
        super(message);
    }

    public PlayerAlreadyInGameException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerAlreadyInGameException(Throwable cause) {
        super(cause);
    }
}
