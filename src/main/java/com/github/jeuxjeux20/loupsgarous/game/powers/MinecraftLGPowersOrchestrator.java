package com.github.jeuxjeux20.loupsgarous.game.powers;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.google.inject.Inject;

@OrchestratorScoped
class MinecraftLGPowersOrchestrator implements LGPowersOrchestrator {
    private final MutableLGGameOrchestrator gameOrchestrator;

    @Inject
    MinecraftLGPowersOrchestrator(MutableLGGameOrchestrator gameOrchestrator) {
        this.gameOrchestrator = gameOrchestrator;
    }

    @Override
    public void add(LGPlayer player, LGPower power) {
        gameOrchestrator.state().mustBe(LGGameState.STARTED);

        MutableLGPlayer mutablePlayer = gameOrchestrator.game().ensurePresent(player);

        mutablePlayer.getMutablePowers().put(power.getClass(), power);
    }

    @Override
    public boolean remove(LGPlayer player, Class<? extends LGPower> powerClass) {
        gameOrchestrator.state().mustBe(LGGameState.STARTED);

        MutableLGPlayer mutablePlayer = gameOrchestrator.game().ensurePresent(player);

        return mutablePlayer.getMutablePowers().remove(powerClass) != null;
    }
}
