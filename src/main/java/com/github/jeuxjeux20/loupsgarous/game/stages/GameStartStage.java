package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GameStartStage extends AsyncLGGameStage implements CountdownTimedStage {
    private final Countdown countdown;

    @Inject
    public GameStartStage(@Assisted LGGameOrchestrator orchestrator) {
        super(orchestrator);

        this.countdown = new GameStartCountdown();
    }

    @Override
    public CompletableFuture<Void> run() {
        return countdown.start();
    }

    @Override
    public @Nullable String getName() {
        return null;
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    private class GameStartCountdown extends Countdown {
        public GameStartCountdown() {
            super(orchestrator.getPlugin(), 5);
        }

        @Override
        protected void onTick() {
            if (orchestrator.getState() != LGGameState.READY_TO_START) {
                setTimer(getBiggestTimerValue());
                return;
            }

            if (getTimer() == 0) return;

            String message = ChatColor.BLUE + "La partie va commencer dans " +
                             ChatColor.YELLOW + getTimer() +
                             ChatColor.BLUE + " secondes.";
            orchestrator.sendToEveryone(message);
        }
    }
}
