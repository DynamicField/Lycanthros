package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GameEndStage extends AsyncLGGameStage implements CountdownTimedStage {
    private final TickEventCountdown countdown;

    @Inject
    public GameEndStage(@Assisted LGGameOrchestrator orchestrator) {
        super(orchestrator);
        countdown = new TickEventCountdown(this, 15);
    }

    @Override
    public CompletableFuture<Void> run() {
        return countdown.start();
    }

    @Override
    public @NotNull String getName() {
        return "Fin !";
    }

    @Override
    public TickEventCountdown getCountdown() {
        return countdown;
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.YELLOW;
    }
}
