package com.github.jeuxjeux20.loupsgarous;

import com.google.common.base.Preconditions;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.CompletableSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Countdown {
    private final CompletableSubject taskSubject = CompletableSubject.create();
    private final PublishSubject<Snapshot> tickSubject = PublishSubject.create();
    private @Nullable Task countdownTask;

    private int timer;
    private int biggestTimerValue;
    private Snapshot startSnapshot;
    private State state = State.READY;
    private boolean paused = false;

    public Countdown(int timerSeconds) {
        this.timer = timerSeconds;
        this.biggestTimerValue = timerSeconds;
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

    public final Completable start() {
        Preconditions.checkState(state == State.READY, "The countdown must be ready");
        state = State.RUNNING;
        startSnapshot = takeSnapshot();

        onStart();
        if (!paused) {
            startTask(false);
        }

        return taskSubject;
    }

    public final void tryInterrupt() {
        if (state != State.FINISHED) {
            interrupt();
        }
    }

    public final void interrupt() {
        Preconditions.checkState(state != State.FINISHED, "The countdown must not be finished.");

        timer = 0;
        finish();
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
        tickSubject.onNext(takeSnapshot());

        if (timer == 0 && state != State.FINISHED) {
            finish();
        }
    }

    private void finish() {
        Preconditions.checkState(state != State.FINISHED, "The countdown must not be finished.");
        state = State.FINISHED;

        stopTask();

        try {
            onFinish();
            taskSubject.onComplete();
        } catch (Throwable e) {
            taskSubject.onError(e);
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

    public Observable<Snapshot> tickUpdates() {
        return tickSubject;
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
