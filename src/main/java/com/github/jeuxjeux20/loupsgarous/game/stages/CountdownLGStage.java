package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * The base class for stages that use a {@link Countdown}.
 * <p>
 * The {@link Countdown} is created using {@link #createCountdown()}, which can
 * be retrieved using {@link #getCountdown()}. This is a lazily-stored value, also used
 * for {@link CountdownTimedStage}.<br>
 * <p>
 * Inheritors of this class must use the {@link #start()} method (instead of {@link #execute()}) to
 * execute actions before the stage (and the countdown) starts.
 */
public abstract class CountdownLGStage extends RunnableLGStage implements CountdownTimedStage {
    private @Nullable Countdown countdown;

    public CountdownLGStage(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected final CompletableFuture<Void> execute() {
        start();
        return getCountdown().start();
    }

    protected void start() {
    }

    protected abstract Countdown createCountdown();

    @Override
    public Countdown getCountdown() {
        if (countdown == null) {
            countdown = createCountdown();
        }

        return countdown;
    }
}
