package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageChangedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageChangingEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageEndedEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.overrides.StageOverride;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
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

class MinecraftLGStagesOrchestrator implements LGStagesOrchestrator {
    private final LGGameOrchestrator gameOrchestrator;

    private final LinkedList<RunnableLGStage.Factory<?>> stageFactories;
    private @Nullable ListIterator<RunnableLGStage.Factory<?>> stageIterator = null;
    private @Nullable RunnableLGStage currentStage = null;
    private @Nullable LGStageChangingEvent currentStageEvent = null;
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
    public @NotNull LGStage current() {
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
        RunnableLGStage lastStage = currentStage;
        LGStageChangingEvent lastEvent = currentStageEvent;

        LGStageChangingEvent event = new LGStageChangingEvent(stage);
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

        currentStage = stage;
        stage.bind(() -> Events.call(new LGStageEndedEvent(stage)));

        Events.call(new LGStageChangedEvent(stage));

        return stage.run().exceptionally(this::handleStageException);
    }

    private Void handleStageException(Throwable ex) {
        // We cancelled it, no need to log.
        if (!(ex instanceof CancellationException)) {
            logger.log(Level.SEVERE, "Unhandled exception while running stage: " + currentStage, ex);
        }
        throw ex instanceof CompletionException ?
                (CompletionException) ex : new CompletionException(ex);
    }

    private Void handlePostStageException(Throwable ex) {
        if (ex.getCause() instanceof CancellationException) return null;
        logger.log(Level.SEVERE, "Unhandled exception while the game was running the next stage.", ex);
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
