package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Countdown {
    protected final Plugin plugin;
    private final CompletableFuture<Void> future = new CompletableFuture<>();
    private int countdownTaskId = -1;
    private int timer;
    private int biggestTimerValue;
    private boolean hasBeenRan;

    public Countdown(Plugin plugin, int timerSeconds) {
        this.plugin = plugin;
        this.timer = timerSeconds;
        this.biggestTimerValue = timerSeconds;
    }

    public boolean hasBeenRan() {
        return hasBeenRan;
    }

    public boolean isRunning() {
        return countdownTaskId != -1;
    }

    public final CompletableFuture<Void> start() {
        Preconditions.checkState(!hasBeenRan(), "The countdown has already been ran");
        hasBeenRan = true;

        onStart();
        startTask();

        future.exceptionally(e -> {
            if (e instanceof CancellationException) {
                complete(true);
            }
            return null;
        });
        return future;
    }

    private void startTask() {
        countdownTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            int oldTimer = this.timer;

            onTick();

            if (oldTimer == this.timer) {  // Do not decrement the timer if it has changed in onTick()
                if (timer == 0) {
                    complete(false);
                }
                timer--;
            }
        }, 0L, 20L);
    }

    private void complete(boolean cancelled) {
        Bukkit.getServer().getScheduler().cancelTask(countdownTaskId);

        if (!cancelled) {
            onFinish();
            future.complete(null);
        }
    }

    protected void onStart() {
    }

    protected void onTick() {
    }

    protected void onFinish() {
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        Preconditions.checkState(!future.isDone(), "The countdown has already finished.");
        Preconditions.checkArgument(timer >= 0, "The timer must not be negative.");

        if (timer == 0) {
            interrupt();
            return;
        }

        if (this.biggestTimerValue < timer) biggestTimerValue = timer;

        this.timer = timer;
    }

    public final void interrupt() {
        timer = 0;
        complete(false);
    }

    public int getBiggestTimerValue() {
        return biggestTimerValue;
    }

    public void resetBiggestTimerValue() {
        this.biggestTimerValue = this.timer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(int time) {
        return new Builder().time(time);
    }

    public static Consumer<Builder> syncWith(Countdown countdown) {
        return builder -> builder
                .time(countdown.getTimer())
                .start(countdown::start)
                .finished(countdown::interrupt);
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

        public Builder time(int time) {
            this.time = time;
            return this;
        }

        public Countdown build(LGGameOrchestrator orchestrator) {
            return build(orchestrator.getPlugin());
        }

        public Countdown build(Plugin plugin) {
            return new Countdown(plugin, time) {
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
