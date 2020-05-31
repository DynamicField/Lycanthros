package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

public class Countdown {
    protected final Plugin plugin;
    private final CompletableFuture<Void> future = new CompletableFuture<>();
    private int countdownTaskId = -1;
    private int timer;
    private int biggestTimerValue;
    private boolean timerChanged;
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
        startTask();
        return future;
    }

    private void startTask() {
        Preconditions.checkState(countdownTaskId == -1, "There already is a task");
        countdownTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (future.isCancelled()) {
                complete(true);
                return;
            }
            timerChanged = false;
            onTick();
            if (timer == 0) {
                complete(false);
            } else if (!timerChanged) {  // Do not decrement the timer if it has changed in onTick()
                timer--;
            }
        }, 0L, 20L);
    }

    private void complete(boolean cancelled) {
        Preconditions.checkState(countdownTaskId != -1, "There is not task to end.");
        Bukkit.getServer().getScheduler().cancelTask(countdownTaskId);
        countdownTaskId = -1;

        if (!cancelled) {
            future.complete(null);
        }
    }

    protected void onTick() {}

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
        timerChanged = true;
    }

    public final void interrupt() {
        timer = 0;
        complete(false);
    }

    public int getBiggestTimerValue() {
        return biggestTimerValue;
    }
}
