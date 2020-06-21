package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageEndedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageEndingEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageStartedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageStartingEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.overrides.StageOverride;
import com.github.jeuxjeux20.loupsgarous.util.FutureExceptionUtils;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

class MinecraftLGStagesOrchestrator implements LGStagesOrchestrator {
    private final LGGameOrchestrator gameOrchestrator;

    private final LinkedList<RunnableLGStage.Factory<?>> stageFactories;
    private @Nullable ListIterator<RunnableLGStage.Factory<?>> stageIterator = null;
    private @Nullable RunnableLGStage currentStage = null;
    private @Nullable LGStageStartingEvent currentStageEvent = null;
    private final Set<StageOverride> stageOverrides;
    private final Logger logger;

    @Inject
    MinecraftLGStagesOrchestrator(@Assisted LGGameOrchestrator gameOrchestrator,
                                  Set<RunnableLGStage.Factory<?>> stageFactories,
                                  Set<StageOverride> stageOverrides,
                                  LoupsGarous plugin) {
        this.gameOrchestrator = gameOrchestrator;
        this.stageFactories = new LinkedList<>(stageFactories);
        this.stageOverrides = stageOverrides;
        this.logger = plugin.getLogger();

        gameOrchestrator.bind(new CurrentStageTerminable());
    }

    @Override
    public void insert(RunnableLGStage.Factory<?> stage) {
        if (stageIterator == null) {
            stageFactories.addFirst(stage);
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
        CompletionStage<Void> future = tryChangeStage(stage);
        if (future != null) return future; // If it isn't null, it's a cancelled future!

        currentStage = stage;

        RunnableLGStage.Task stageTask = stage.run();
        Events.call(new LGStageStartedEvent(stage));

        // Call the LGStageEndingEvent just before finish() gets executed.
        stageTask.beforeFinish().thenRun(() -> Events.call(new LGStageEndingEvent(stage)));

        // And then call LGStageEndedEvent after the whole stage ended.
        return stageTask.main()
                .thenRun(() -> Events.call(new LGStageEndedEvent(stage)))
                .exceptionally(this::handleStageException);
    }

    private CompletionStage<Void> tryChangeStage(RunnableLGStage stage) {
        RunnableLGStage lastStage = currentStage;
        LGStageStartingEvent lastEvent = currentStageEvent;

        LGStageStartingEvent event = new LGStageStartingEvent(stage);
        currentStageEvent = event;

        Events.call(event);

        if (event.isCancelled()) {
            // Some listeners cancelled the event
            // or changed the stage, don't execute the initial one,
            // and return a cancelled future.
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.cancel(true);
            return future;
        }

        // Cancel the previous stage and its event before running the new one.
        if (lastStage != null && !lastStage.isClosed()) lastStage.closeAndReportException();
        if (lastEvent != null) lastEvent.setCancelled(true);
        return null;
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
