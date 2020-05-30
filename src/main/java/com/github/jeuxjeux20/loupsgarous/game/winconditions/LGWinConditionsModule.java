package com.github.jeuxjeux20.loupsgarous.game.winconditions;

public final class LGWinConditionsModule extends WinConditionsModule {
    @Override
    protected void configureWinConditions() {
        addWinCondition(EveryoneDeadWinCondition.class);
        addWinCondition(CoupleWinCondition.class);
        addWinCondition(LoupsGarousWinCondition.class);
        addWinCondition(VillageWinCondition.class);
    }
}
