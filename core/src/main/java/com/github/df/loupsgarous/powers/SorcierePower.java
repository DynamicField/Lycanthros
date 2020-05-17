package com.github.df.loupsgarous.powers;

import com.google.common.base.Preconditions;

public final class SorcierePower extends LGPower {
    private boolean healPotionAvailable = true;
    private boolean killPotionAvailable = true;

    public SorcierePower(Object source) {
        super(source);
    }

    @Override
    public String getName() {
        return "Pouvoir de la sorcière";
    }

    public void useHealPotion() {
        Preconditions.checkState(healPotionAvailable, "No heal potion to use.");
        healPotionAvailable = false;
    }

    public boolean isHealPotionAvailable() {
        return healPotionAvailable;
    }

    public void setHealPotionAvailable(boolean healPotionAvailable) {
        this.healPotionAvailable = healPotionAvailable;
    }

    public void useKillPotion() {
        Preconditions.checkState(killPotionAvailable, "No kill potion to use.");
        killPotionAvailable = false;
    }

    public boolean isKillPotionAvailable() {
        return killPotionAvailable;
    }

    public void setKillPotionAvailable(boolean killPotionAvailable) {
        this.killPotionAvailable = killPotionAvailable;
    }
}
