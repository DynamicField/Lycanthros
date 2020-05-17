package com.github.df.loupsgarous.mechanic;

public class RevelationResult {
    private boolean revealed;

    public RevelationResult(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public void reveal() {
        setRevealed(true);
    }

    public void hide() {
        setRevealed(false);
    }
}
