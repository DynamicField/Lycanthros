package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * The base class for phases that use a {@link Countdown}.
 * <p>
 * The {@link Countdown} is created using {@link #createCountdown()}, which can
 * be retrieved using {@link #getCountdown()}. This is a lazily-stored value, also used
 * for {@link CountdownTimedPhase}.<br>
 * <p>
 * Inheritors of this class must use the {@link #start()} method (instead of {@link #execute()}) to
 * execute actions before the phase (and the countdown) starts.
 */
public abstract class CountdownLGPhase extends RunnableLGPhase implements CountdownTimedPhase {
    private @Nullable Countdown countdown;

    public CountdownLGPhase(LGGameOrchestrator orchestrator) {
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
