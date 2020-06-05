package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.events.stage.LGStageChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.overrides.StageOverride;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinecraftLGStagesOrchestrator implements LGStagesOrchestrator {
    private final LGGameOrchestrator gameOrchestrator;

    private final LinkedList<RunnableLGGameStage.Factory<?>> stageFactories;
    private @Nullable ListIterator<RunnableLGGameStage.Factory<?>> stageIterator = null;
    private @Nullable RunnableLGGameStage currentStage = null;
    private @Nullable CompletableFuture<Void> currentStageFuture = null;
    private @Nullable LGStageChangeEvent currentStageEvent = null;
    private final Set<StageOverride> stageOverrides;
    private final Logger logger;

    @Inject
    MinecraftLGStagesOrchestrator(@Assisted LGGameOrchestrator gameOrchestrator,
                                  Set<RunnableLGGameStage.Factory<?>> stageFactories,
                                  Set<StageOverride> stageOverrides,
                                  LoupsGarous plugin) {
        this.gameOrchestrator = gameOrchestrator;
        this.stageFactories = new LinkedList<>(stageFactories);
        this.stageOverrides = stageOverrides;
        this.logger = plugin.getLogger();

        gameOrchestrator.bind(new CurrentStageTerminable());
    }

    @Override
    public void add(RunnableLGGameStage.Factory<?> stage) {
        if (stageIterator == null) {
            stageFactories.add(stage);
        } else {
            stageIterator.add(stage);
            stageIterator.previous();
        }
    }

    @Override
    public void next() {
        if (callStageOverride()) return;

        if (stageFactories.size() == 0)
            throw new IllegalStateException("No stages have been found.");

        if (stageIterator == null || !stageIterator.hasNext())
            stageIterator = stageFactories.listIterator(); // Reset the iterator

        RunnableLGGameStage.Factory<?> factory = stageIterator.next();
        RunnableLGGameStage stage = factory.create(gameOrchestrator);

        if (stage.isTemporary())
            stageIterator.remove();

        if (stage.shouldRun()) {
            updateAndRunCurrentStage(stage).thenRun(this::next).exceptionally(this::handleFutureException);
        } else {
            next();
        }
    }

    private boolean callStageOverride() {
        Optional<StageOverride> activeStageOverride = stageOverrides.stream()
                .filter(x -> x.shouldOverride(gameOrchestrator))
                .findFirst();

        activeStageOverride.ifPresent(stageOverride -> {
            if (stageOverride.getStageClass().isInstance(currentStage)) return;

            RunnableLGGameStage stage = stageOverride.getStageFactory().create(gameOrchestrator);

            updateAndRunCurrentStage(stage)
                    .thenRun(() -> stageOverride.onceComplete(gameOrchestrator))
                    .exceptionally(this::handleFutureException);
        });

        return activeStageOverride.isPresent();
    }

    private CompletionStage<Void> updateAndRunCurrentStage(RunnableLGGameStage stage) {
        CompletableFuture<Void> lastFuture = currentStageFuture;
        LGStageChangeEvent lastEvent = currentStageEvent;

        LGStageChangeEvent event = new LGStageChangeEvent(gameOrchestrator, stage);
        currentStageEvent = event;

        gameOrchestrator.callEvent(event);

        if (event.isCancelled()) {
            // Some listeners cancelled the event
            // or changed the stage, don't run the initial one,
            // and return a cancelled future.
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.cancel(true);
            return future;
        }

        // Cancel the previous stage stuff before running the new one
        if (lastFuture != null && !lastFuture.isDone()) lastFuture.cancel(true);
        if (lastEvent != null) lastEvent.setCancelled(true);

        currentStage = stage;

        CompletableFuture<Void> future = stage.run();
        currentStageFuture = future;

        return future.exceptionally(this::handleStageException);
    }

    private Void handleStageException(Throwable ex) {
        // We cancelled it, no need to log.
        if (!(ex instanceof CancellationException)) {
            logger.log(Level.SEVERE, "Unhandled exception while running stage: " + currentStage, ex);
        }
        throw ex instanceof CompletionException ?
                (CompletionException) ex : new CompletionException(ex);
    }

    private Void handleFutureException(Throwable ex) {
        if (ex.getCause() instanceof CancellationException) return null;
        logger.log(Level.SEVERE, "Unhandled exception while the game was running the next stage.", ex);
        return null;
    }

    @Override
    public @NotNull LGGameStage current() {
        return currentStage == null ? new LGGameStage.Null() : currentStage;
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return gameOrchestrator;
    }

    private final class CurrentStageTerminable implements Terminable {
        @Override
        public void close() {
            if (currentStageFuture != null && !currentStageFuture.isDone())
                currentStageFuture.cancel(true);
        }
    }
}
