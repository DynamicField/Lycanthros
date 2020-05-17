package com.github.df.loupsgarous.game;

/**
 * A set a values containing the outcome of a game for a player.
 */
public enum PlayerGameOutcome {
    /**
     * Indicates that a player won the game.
     */
    WIN,
    /**
     * Indicates that a player lost the game.
     */
    LOSE,
    /**
     * Indicates that we have no idea if the player won or not.<br>
     * ¯\_(ツ)_/¯
     */
    SHRUG;

    public static final String SHRUG_EMOJI = "\u00AF\\_(\u30C4)_/\u00AF"; // ¯\_(ツ)_/¯

    public static PlayerGameOutcome wonWhen(Boolean won) {
        return won ? WIN : LOSE;
    }

    public static PlayerGameOutcome lostWhen(Boolean lost) {
        return lost ? LOSE : WIN;
    }
}
