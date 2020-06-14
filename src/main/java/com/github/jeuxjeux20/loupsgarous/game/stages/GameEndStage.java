package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.boss.BarColor;

import java.util.concurrent.CompletableFuture;

public class GameEndStage extends CountdownLGStage {
    @Inject
    GameEndStage(@Assisted LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(15);
    }

    @Override
    public String getName() {
        return "Fin !";
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.YELLOW;
    }
}
