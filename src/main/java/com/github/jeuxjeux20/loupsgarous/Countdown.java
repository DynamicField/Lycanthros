package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.loupsgarous.event.CountdownTickEvent;
import com.github.jeuxjeux20.loupsgarous.util.FutureExceptionUtils;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Countdown implements Terminable, TerminableConsumer {
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    private final CompletableFuture<Void> future = new CompletableFuture<>();
    private @Nullable Task countdownTask;

    private int timer;
    private int biggestTimerValue;

    private Snapshot startSnapshot;

    private State state = State.READY;
    private boolean paused = false;

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

    // Start & Interrupt

    public final CompletableFuture<Void> start() {
        Preconditions.checkState(state == State.READY, "The countdown must be ready");
        state = State.RUNNING;
        startSnapshot = takeSnapshot();

        onStart();
        if (!paused) {
            startTask(false);
        }

        future.whenComplete((r, e) -> {
            if (FutureExceptionUtils.isCancellation(e)) {
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

    private void startTask(boolean resume) {
        if (!resume) {
            handleTick();
        }
        stopTask();
        countdownTask = Schedulers.sync().runRepeating(() -> {
            timer--;

            handleTick();
        }, 20L, 20L);
    }

    private void stopTask() {
        if (countdownTask != null)
            countdownTask.stop();
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

        stopTask();

        terminableRegistry.closeAndReportException();

        if (!cancelled) {
            try {
                onFinish();
                future.complete(null);
            } catch (Throwable e) {
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
        if (this.biggestTimerValue < timer) {
            biggestTimerValue = timer;
        }

        this.timer = timer;

        if (state == State.RUNNING) {
            startTask(true);
        }
    }

    public State getState() {
        return state;
    }

    public boolean is(State state) {
        return this.state == state;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        Preconditions.checkState(state != State.FINISHED, "The countdown must not be finished.");

        this.paused = paused;
        if (state == State.RUNNING) {
            if (paused) {
                stopTask();
            } else {
                startTask(true);
            }
        }
    }

    public int getBiggestTimerValue() {
        return biggestTimerValue;
    }

    public void resetBiggestTimerValue() {
        this.biggestTimerValue = this.timer;
    }

    public Snapshot takeSnapshot() {
        return new Snapshot(timer);
    }

    public Snapshot getStartSnapshot() {
        return startSnapshot;
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

    public static final class Snapshot {
        private final long timeMillis;
        private final int timer;

        public Snapshot(int timer) {
            this(System.currentTimeMillis(), timer);
        }

        public Snapshot(long timeMillis, int timer) {
            this.timeMillis = timeMillis;
            this.timer = timer;
        }

        public long getTimeMillis() {
            return timeMillis;
        }

        public int getTimer() {
            return timer;
        }

        public int getTimerNow() {
            return Math.max(0, timer - (int) ((System.currentTimeMillis() - timeMillis) / 1000f));
        }
    }
}
