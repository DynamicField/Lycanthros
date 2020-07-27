package com.github.jeuxjeux20.loupsgarous.stages;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.stages.descriptor.LGStageDescriptor;
import com.github.jeuxjeux20.loupsgarous.stages.overrides.StageOverride;
import com.github.jeuxjeux20.loupsgarous.util.FutureExceptionUtils;
import com.google.inject.Inject;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

@OrchestratorScoped
public class LGStagesOrchestrator extends AbstractOrchestratorComponent {
    private final LinkedList<RunnableLGStage.Factory<?>> stageFactories;
    private final LGStageDescriptor.Registry descriptorRegistry;
    private ListIterator<RunnableLGStage.Factory<?>> stageIterator;
    private @Nullable RunnableLGStage currentStage;
    private final Set<StageOverride> stageOverrides;
    private final Logger logger;

    @Inject
    LGStagesOrchestrator(LGGameOrchestrator orchestrator,
                         Set<RunnableLGStage.Factory<?>> stageFactories,
                         Set<StageOverride> stageOverrides,
                         LGStageDescriptor.Registry descriptorRegistry) {
        super(orchestrator);
        this.stageFactories = new LinkedList<>(stageFactories);
        this.descriptorRegistry = descriptorRegistry;
        this.stageIterator = this.stageFactories.listIterator();
        this.stageOverrides = stageOverrides;
        this.logger = orchestrator.logger();

        bind(new CurrentStageTerminable());
    }

    /**
     * Insert the given stage factory to the current game that will be created and run as soon as
     * possible (LIFO).
     *
     * @param stageFactory the stage factory to insert
     */
    public void insert(RunnableLGStage.Factory<?> stageFactory) {
        stageIterator.add(stageFactory);
        stageIterator.previous();
    }

    /**
     * Cancels the current stages, if any, and runs the next one.
     * <p>
     * Note that some {@link StageOverride}s might prevent the execution of the next stage, for
     * example, if the game is {@linkplain LGGameState#WAITING_FOR_PLAYERS waiting for players},
     * this method will ensure that the current stage is an instance of {@link GameStartStage}.
     */
    public void next() {
        if (callStageOverride()) return;

        if (stageFactories.size() == 0)
            throw new IllegalStateException("No stages have been found.");

        if (!stageIterator.hasNext())
            stageIterator = stageFactories.listIterator(); // Reset the iterator

        RunnableLGStage.Factory<?> factory = stageIterator.next();
        RunnableLGStage stage = factory.create(orchestrator);
        LGStageDescriptor descriptor = descriptorRegistry.get(stage.getClass());

        if (descriptor.isTemporary())
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

    /**
     * Gets the current stage, or an instance of {@link LGStage.Null} if there isn't any stage
     * running right now.
     *
     * @return the current stage, or {@link LGStage.Null}
     */
    public LGStage current() {
        return currentStage == null ? new LGStage.Null(orchestrator) : currentStage;
    }

    /**
     * Returns the descriptor registry.
     *
     * @return the descriptor registry
     */
    public LGStageDescriptor.Registry descriptors() {
        return descriptorRegistry;
    }

    private boolean callStageOverride() {
        Optional<StageOverride> activeStageOverride = stageOverrides.stream()
                .filter(x -> x.shouldOverride(orchestrator))
                .findFirst();

        activeStageOverride.ifPresent(stageOverride -> {
            if (stageOverride.getStageClass().isInstance(currentStage)) return;

            RunnableLGStage stage = stageOverride.getStageFactory().create(orchestrator);

            runStage(stage)
                    .thenRun(() -> stageOverride.onceComplete(orchestrator))
                    .exceptionally(this::handlePostStageException);
        });

        return activeStageOverride.isPresent();
    }

    private CompletionStage<Void> runStage(RunnableLGStage stage) {
        updateStage(stage);

        return stage.run().exceptionally(this::handleStageException);
    }

    private void updateStage(RunnableLGStage stage) {
        if (currentStage != null && !currentStage.isClosed())
            currentStage.closeAndReportException();

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
        return orchestrator;
    }

    private final class CurrentStageTerminable implements Terminable {
        @Override
        public void close() {
            if (currentStage != null && !currentStage.isClosed())
                currentStage.closeAndReportException();
        }
    }
}
