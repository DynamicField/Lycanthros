package com.github.jeuxjeux20.loupsgarous.game.lobby;

public class InaccessibleLobbyException extends PlayerJoinException {
    public InaccessibleLobbyException() {
    }

    public InaccessibleLobbyException(String message) {
        super(message);
    }

    public InaccessibleLobbyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InaccessibleLobbyException(Throwable cause) {
        super(cause);
    }

    public static InaccessibleLobbyException lobbyFull() {
        return new InaccessibleLobbyException("The lobby is full.") {
            @Override
            public String getLocalizedMessage() {
                return "La partie est pleine.";
            }
        };
    }

    public static InaccessibleLobbyException lobbyLocked() {
        return new InaccessibleLobbyException("The game has already started.") {
            @Override
            public String getLocalizedMessage() {
                return "La partie a déjà commencé.";
            }
        };
    }
}
