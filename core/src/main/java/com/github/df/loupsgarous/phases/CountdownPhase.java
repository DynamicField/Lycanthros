package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.Countdown;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import org.jetbrains.annotations.Nullable;

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
public abstract class CountdownPhase extends RunnablePhase implements CountdownTimedPhase {
    private @Nullable Countdown countdown;

    public CountdownPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected final PhaseTask execute() {
        start();
        return new PhaseTask(getCountdown().start()) {
            @Override
            public boolean isRunning() {
                return getCountdown().getState() == Countdown.State.RUNNING;
            }

            @Override
            public boolean stop() {
                if (isRunning()) {
                    getCountdown().interrupt();
                    return true;
                }
                return false;
            }
        };
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
