package com.github.jeuxjeux20.loupsgarous.game.lobby;

import org.bukkit.entity.Player;

public class PlayerAlreadyPresentException extends PlayerJoinException {
    public PlayerAlreadyPresentException() {
    }

    public PlayerAlreadyPresentException(Player player) {
        this("The player '" + player + "' is already present.");
    }

    public PlayerAlreadyPresentException(String message) {
        super(message);
    }

    public PlayerAlreadyPresentException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerAlreadyPresentException(Throwable cause) {
        super(cause);
    }
}
