package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.events.stage.LGStageChangeEvent;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinecraftLGStagesOrchestrator implements LGStagesOrchestrator {
    private final LGGameOrchestrator gameOrchestrator;

    private final LinkedList<AsyncLGGameStage.Factory<?>> stageFactories;
    private @Nullable ListIterator<AsyncLGGameStage.Factory<?>> stageIterator = null;
    private @Nullable AsyncLGGameStage currentStage = null;
    private @Nullable CompletableFuture<Void> currentStageFuture = null;
    private final Logger logger;

    @Inject
    public MinecraftLGStagesOrchestrator(@Assisted LGGameOrchestrator gameOrchestrator,
                                         Set<AsyncLGGameStage.Factory<?>> stageFactories,
                                         LoupsGarous plugin) {
        this.gameOrchestrator = gameOrchestrator;
        this.stageFactories = new LinkedList<>(stageFactories);
        this.logger = plugin.getLogger();

        gameOrchestrator.bind(new CurrentStageTerminable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(AsyncLGGameStage.Factory<?> stage) {
        if (stageIterator == null) {
            stageFactories.add(stage);
        } else {
            stageIterator.add(stage);
            stageIterator.previous();
        }
    }

    @Override
    public void next() {
        if (currentStageFuture != null && !currentStageFuture.isDone()) currentStageFuture.cancel(true);

        if (callSpecialStages()) return;

        if (stageFactories.size() == 0)
            throw new IllegalStateException("No stages have been found.");

        if (stageIterator == null || !stageIterator.hasNext())
            stageIterator = stageFactories.listIterator(); // Reset the iterator

        AsyncLGGameStage.Factory<?> factory = stageIterator.next();
        AsyncLGGameStage stage = factory.create(gameOrchestrator);

        if (stage.isTemporary())
            stageIterator.remove();

        if (stage.shouldRun()) {
            updateAndRunCurrentStage(stage).thenRun(this::next);
        } else {
            next();
        }
    }

    private boolean callSpecialStages() {
        LGGameState state = gameOrchestrator.getState();

        if (state == LGGameState.READY_TO_START || state == LGGameState.WAITING_FOR_PLAYERS) {
            if (!(currentStage instanceof GameStartStage)) {
                GameStartStage stage = new GameStartStage(gameOrchestrator);
                updateAndRunCurrentStage(stage).thenRun(gameOrchestrator::start).exceptionally(ex -> {
                    if (ex.getCause() instanceof CancellationException) return null;
                    logger.log(Level.SEVERE, "Unhandled exception while the game was running: ", ex);
                    return null;
                });
            }
            return true;
        }
        if (state == LGGameState.FINISHED) {
            if (!(currentStage instanceof GameEndStage)) {
                GameEndStage stage = new GameEndStage(gameOrchestrator);
                updateAndRunCurrentStage(stage).thenRun(gameOrchestrator::delete).exceptionally(ex -> {
                    if (ex.getCause() instanceof CancellationException) return null;
                    logger.log(Level.SEVERE, "Unhandled exception while deleting the game: ", ex);
                    return null;
                });
            }
            return true;
        }
        return false;
    }

    private CompletionStage<Void> updateAndRunCurrentStage(AsyncLGGameStage stage) {
        currentStage = stage;
        gameOrchestrator.callEvent(new LGStageChangeEvent(gameOrchestrator, stage));
        CompletableFuture<Void> future = stage.run();
        currentStageFuture = future;
        return future.exceptionally(ex -> {
            // We cancelled it, no need to log.
            if (!(ex instanceof CancellationException)) {
                logger.log(Level.SEVERE, "Unhandled exception while running stage: " + stage, ex);
            }
            throw ex instanceof CompletionException ?
                    (CompletionException) ex : new CompletionException(ex);
        });
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
