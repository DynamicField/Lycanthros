package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.stages.overrides.StageOverride;
import com.github.jeuxjeux20.loupsgarous.util.FutureExceptionUtils;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

class MinecraftLGStagesOrchestrator implements LGStagesOrchestrator {
    private final LGGameOrchestrator gameOrchestrator;

    private final LinkedList<RunnableLGStage.Factory<?>> stageFactories;
    private ListIterator<RunnableLGStage.Factory<?>> stageIterator;
    private @Nullable RunnableLGStage currentStage = null;
    private final Set<StageOverride> stageOverrides;
    private final Logger logger;

    @Inject
    MinecraftLGStagesOrchestrator(@Assisted LGGameOrchestrator gameOrchestrator,
                                  Set<RunnableLGStage.Factory<?>> stageFactories,
                                  Set<StageOverride> stageOverrides,
                                  LoupsGarous plugin) {
        this.gameOrchestrator = gameOrchestrator;
        this.stageFactories = new LinkedList<>(stageFactories);
        this.stageIterator = this.stageFactories.listIterator();
        this.stageOverrides = stageOverrides;
        this.logger = gameOrchestrator.logger();

        gameOrchestrator.bind(new CurrentStageTerminable());
    }

    @Override
    public void insert(RunnableLGStage.Factory<?> stage) {
        stageIterator.add(stage);
        stageIterator.previous();
    }

    @Override
    public void next() {
        if (callStageOverride()) return;

        if (stageFactories.size() == 0)
            throw new IllegalStateException("No stages have been found.");

        if (!stageIterator.hasNext())
            stageIterator = stageFactories.listIterator(); // Reset the iterator

        RunnableLGStage.Factory<?> factory = stageIterator.next();
        RunnableLGStage stage = factory.create(gameOrchestrator);

        if (stage.isTemporary())
            stageIterator.remove();

        if (stage.shouldRun()) {
            runStage(stage).thenRun(this::next).exceptionally(this::handlePostStageException);
        } else {
            // Close the stage because we didn't run it.
            //
            // NOTE: That's one of the problems of having close() and shouldRun() together,
            // you might forget to close it when it shouldn't run, and also create
            // unnecessary objects in the constructor.
            // I can't really think of an alternative that is nearly as pleasant to use
            // as the shouldRun() method.
            // While something such as StageOverride somewhat fixes the whole issue,
            // requiring an extra class just for a condition seems really annoying.
            // For now, this works. Let's not complain :D
            stage.closeAndReportException();
            next();
        }
    }

    @Override
    public LGStage current() {
        return currentStage == null ? new LGStage.Null(gameOrchestrator) : currentStage;
    }

    private boolean callStageOverride() {
        Optional<StageOverride> activeStageOverride = stageOverrides.stream()
                .filter(x -> x.shouldOverride(gameOrchestrator))
                .findFirst();

        activeStageOverride.ifPresent(stageOverride -> {
            if (stageOverride.getStageClass().isInstance(currentStage)) return;

            RunnableLGStage stage = stageOverride.getStageFactory().create(gameOrchestrator);

            runStage(stage)
                    .thenRun(() -> stageOverride.onceComplete(gameOrchestrator))
                    .exceptionally(this::handlePostStageException);
        });

        return activeStageOverride.isPresent();
    }

    private CompletionStage<Void> runStage(RunnableLGStage stage) {
        updateStage(stage);

        return stage.run().exceptionally(this::handleStageException);
    }

    private void updateStage(RunnableLGStage stage) {
        if (currentStage != null && !currentStage.isClosed()) currentStage.closeAndReportException();

        currentStage = stage;
    }

    private Void handleStageException(Throwable ex) {
        if (FutureExceptionUtils.isCancellation(ex)) {
            // That's cancelled, rethrow to avoid executing further actions.

            throw FutureExceptionUtils.asCompletionException(ex);
        } else {
            // If something wrong happens, let's just log and continue.
            // We still want the game to continue!

            logger.log(Level.SEVERE, "Unhandled exception while running stage: " + currentStage, ex);
            return null;
        }
    }

    private Void handlePostStageException(Throwable ex) {
        if (!FutureExceptionUtils.isCancellation(ex)) {
            logger.log(Level.SEVERE, "Unhandled exception while the game was running the next stage.", ex);
        }
        return null;
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return gameOrchestrator;
    }

    private final class CurrentStageTerminable implements Terminable {
        @Override
        public void close() {
            if (currentStage != null && !currentStage.isClosed())
                currentStage.closeAndReportException();
        }
    }
}
