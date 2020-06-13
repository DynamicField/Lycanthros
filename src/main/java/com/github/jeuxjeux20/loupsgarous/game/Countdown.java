package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.event.CountdownTickEvent;
import com.google.common.base.Preconditions;
import me.lucko.helper.Events;
import me.lucko.helper.Helper;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Countdown implements Terminable, TerminableConsumer {
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    private final CompletableFuture<Void> future = new CompletableFuture<>();
    private @Nullable Task countdownTask;

    private int timer;
    private int biggestTimerValue;

    private State state = State.READY;

    public Countdown(int timerSeconds) {
        this.timer = timerSeconds;
        this.biggestTimerValue = timerSeconds;

        // Just in case the server gets reloaded and for some obscure reason
        // the countdown doesn't get cancelled.
        terminableRegistry.bindWith(Helper.hostPlugin());
    }

    public static Countdown of(int seconds) {
        return new Countdown(seconds);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(int seconds) {
        return new Builder().time(seconds);
    }

    public static Consumer<Builder> syncWith(Countdown countdown) {
        return builder -> builder
                .time(countdown.getTimer())
                .start(countdown::start)
                .finished(countdown::tryInterrupt);
    }

    // Start & Interrupt

    public final CompletableFuture<Void> start() {
        Preconditions.checkState(state == State.READY, "The countdown must be ready");
        state = State.RUNNING;

        onStart();
        startTask();

        future.whenComplete((r, e) -> {
            if (e instanceof CancellationException) {
                finish(true);
            } else if (state == State.RUNNING) {
                // The future has somehow completed while it was running?
                // That's like doing interrupt().
                interrupt();
            }
        });
        return future;
    }

    public final void tryInterrupt() {
        if (state != State.FINISHED) {
            interrupt();
        }
    }

    public final void interrupt() {
        Preconditions.checkState(state != State.FINISHED, "The countdown must not be finished.");

        timer = 0;
        finish(false);
    }

    // Internal stuff

    private void startTask() {
        handleTick();
        countdownTask = Schedulers.sync().runRepeating(() -> {
            timer--;

            handleTick();
        }, 20L, 20L);
    }

    private void handleTick() {
        onTick();
        Events.call(new CountdownTickEvent(this));

        if (timer == 0 && state != State.FINISHED) {
            finish(false);
        }
    }

    private void finish(boolean cancelled) {
        Preconditions.checkState(state != State.FINISHED, "The countdown must not be finished.");
        state = State.FINISHED;

        if (countdownTask != null)
            countdownTask.stop();

        terminableRegistry.closeAndReportException();

        if (!cancelled) {
            try {
                onFinish();
                future.complete(null);
            }
            catch (Throwable e) {
                future.completeExceptionally(e);
            }
        }
    }

    // Event methods

    protected void onStart() {
    }

    protected void onTick() {
    }

    protected void onFinish() {
    }

    // Properties & State

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        Preconditions.checkState(state != State.FINISHED, "The countdown must not be finished.");
        Preconditions.checkArgument(timer >= 0, "The timer must not be negative.");

        if (timer == 0) {
            interrupt();
            return;
        }

        if (this.biggestTimerValue < timer) biggestTimerValue = timer;

        this.timer = timer;
    }

    public State getState() {
        return state;
    }

    public boolean is(State state) {
        return this.state == state;
    }

    public int getBiggestTimerValue() {
        return biggestTimerValue;
    }

    public void resetBiggestTimerValue() {
        this.biggestTimerValue = this.timer;
    }

    // Terminables

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @Override
    public void close() {
        if (state != State.FINISHED) {
            finish(true);
        }
    }

    @Override
    public boolean isClosed() {
        return state == State.FINISHED;
    }

    public enum State {
        READY,
        RUNNING,
        FINISHED
    }

    public static final class Builder {
        private final List<Runnable> tickActions = new ArrayList<>(1);
        private final List<Runnable> finishedActions = new ArrayList<>(1);
        private final List<Runnable> startActions = new ArrayList<>(1);
        private int time;

        public Builder apply(Consumer<Builder> consumer) {
            consumer.accept(this);
            return this;
        }

        public Builder tick(Runnable action) {
            tickActions.add(action);
            return this;
        }

        public Builder start(Runnable action) {
            startActions.add(action);
            return this;
        }

        public Builder finished(Runnable action) {
            finishedActions.add(action);
            return this;
        }

        public Builder time(int seconds) {
            this.time = seconds;
            return this;
        }

        public Countdown build() {
            return new Countdown(time) {
                @Override
                protected void onStart() {
                    startActions.forEach(Runnable::run);
                }

                @Override
                protected void onTick() {
                    tickActions.forEach(Runnable::run);
                }

                @Override
                protected void onFinish() {
                    finishedActions.forEach(Runnable::run);
                }
            };
        }
    }
}
