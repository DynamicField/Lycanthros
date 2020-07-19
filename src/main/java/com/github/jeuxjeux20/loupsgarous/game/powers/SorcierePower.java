package com.github.jeuxjeux20.loupsgarous.game.powers;

import com.google.common.base.Preconditions;

public final class SorcierePower extends LGPower {
    private boolean hasHealPotion;
    private boolean hasKillPotion;

    public SorcierePower(Object source) {
        super(source);
    }

    @Override
    public String getName() {
        return "Pouvoir de la sorcière";
    }

    public void useHealPotion() {
        Preconditions.checkState(hasHealPotion, "No heal potion to use.");
        hasHealPotion = false;
    }

    public boolean hasHealPotion() {
        return hasHealPotion;
    }

    public void setHasHealPotion(boolean hasHealPotion) {
        this.hasHealPotion = hasHealPotion;
    }

    public void useKillPotion() {
        Preconditions.checkState(hasKillPotion, "No kill potion to use.");
        hasKillPotion = false;
    }

    public boolean hasKillPotion() {
        return hasKillPotion;
    }

    public void setHasKillPotion(boolean hasKillPotion) {
        this.hasKillPotion = hasKillPotion;
    }
}
