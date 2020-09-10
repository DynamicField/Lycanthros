package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.AbstractOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhaseDescriptor;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhaseDescriptorRegistry;
import com.github.jeuxjeux20.loupsgarous.util.FutureExceptionUtils;
import com.google.inject.Inject;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.PHASES;

public class LGPhasesOrchestrator extends AbstractOrchestratorComponent {
    private LinkedList<RunnableLGPhase.Factory<?>> phases;
    private final LGPhaseDescriptorRegistry descriptorRegistry;
    private ListIterator<RunnableLGPhase.Factory<?>> phaseIterator;
    private @Nullable RunnableLGPhase currentPhase;
    private final Logger logger;

    @Inject
    LGPhasesOrchestrator(LGGameOrchestrator orchestrator,
                         LGPhaseDescriptorRegistry descriptorRegistry) {
        super(orchestrator);
        this.phases = getPhaseFactories();
        this.descriptorRegistry = descriptorRegistry;
        this.phaseIterator = this.phases.listIterator();
        this.logger = orchestrator.logger();

        bind(new CurrentPhaseTerminable());
    }

    private LinkedList<RunnableLGPhase.Factory<?>> getPhaseFactories() {
        return new LinkedList<>(orchestrator.getGameBox().contents(PHASES));
    }

    /**
     * Insert the given phase factory to the current game that will be created and run as soon as
     * possible (LIFO).
     *
     * @param phaseFactory the phase factory to insert
     */
    public void insert(RunnableLGPhase.Factory<?> phaseFactory) {
        phaseIterator.add(phaseFactory);
        phaseIterator.previous();
    }

    /**
     * If the game is running, cancels the current phase, if any, and runs the next one.
     * <p>
     * Else, makes sure that the current stage is {@link LobbyPhase} or {@link GameEndPhase},
     * depending on the current state of the orchestrator.
     */
    public void next() {
        if (orchestrator.getState() == LGGameState.LOBBY) {
            if (currentPhase instanceof LobbyPhase) {
                return;
            }

            runPhase(new LobbyPhase(orchestrator));
        } else if (orchestrator.getState() == LGGameState.FINISHED) {
            if (currentPhase instanceof GameEndPhase) {
                return;
            }

            runPhase(new GameEndPhase(orchestrator));
        } else if (orchestrator.getState() != LGGameState.STARTED) {
            // The game is not supposed to run phases at that state.
            updatePhase(null);
            return;
        }

        if (phases.size() == 0)
            throw new IllegalStateException("No phases have been found.");

        if (!phaseIterator.hasNext())
            phaseIterator = phases.listIterator(); // Reset the iterator

        RunnableLGPhase.Factory<?> factory = phaseIterator.next();
        RunnableLGPhase phase = factory.create(orchestrator);
        LGPhaseDescriptor descriptor = descriptorRegistry.get(phase.getClass());

        if (descriptor.isTemporary())
            phaseIterator.remove();

        if (phase.shouldRun()) {
            runPhase(phase);
        } else {
            // Close the phase because we didn't run it.
            //
            // NOTE: That's one of the problems of having close() and shouldRun() together,
            // you might forget to close it when it shouldn't run, and also create
            // unnecessary objects in the constructor.
            // I can't really think of an alternative that is nearly as pleasant to use
            // as the shouldRun() method.
            // While something such as PhaseEntry somewhat fixes the whole issue,
            // requiring an extra class just for a condition seems really annoying.
            // For now, this works. Let's not complain :D
            phase.closeAndReportException();
            next();
        }
    }

    /**
     * Gets the current phase, or an instance of {@link LGPhase.Null} if there isn't any phase
     * running right now.
     *
     * @return the current phase, or {@link LGPhase.Null}
     */
    public LGPhase current() {
        return currentPhase == null ? new LGPhase.Null(orchestrator) : currentPhase;
    }

    /**
     * Returns the descriptor registry.
     *
     * @return the descriptor registry
     */
    public LGPhaseDescriptorRegistry descriptors() {
        return descriptorRegistry;
    }

    private void runPhase(RunnableLGPhase phase) {
        updatePhase(phase);

        phase.run()
                .exceptionally(this::handlePhaseException)
                .thenRun(() -> {
                    if (currentPhase == phase) {
                        next();
                    }
                })
                .exceptionally(this::handlePostPhaseException);
    }

    private void updatePhase(@Nullable RunnableLGPhase phase) {
        if (currentPhase != null && !currentPhase.isClosed())
            currentPhase.closeAndReportException();

        currentPhase = phase;
    }

    private Void handlePhaseException(Throwable ex) {
        if (FutureExceptionUtils.isCancellation(ex)) {
            // That's cancelled, rethrow to avoid executing further actions.

            throw FutureExceptionUtils.asCompletionException(ex);
        } else {
            // If something wrong happens, let's just log and continue.
            // We still want the game to continue!

            logger.log(Level.SEVERE, "Unhandled exception while running phase: " + currentPhase, ex);
            return null;
        }
    }

    private Void handlePostPhaseException(Throwable ex) {
        if (!FutureExceptionUtils.isCancellation(ex)) {
            logger.log(Level.SEVERE, "Unhandled exception while the game was running the next phase.", ex);
        }
        // TODO: What do we do here?
        return null;
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    private final class CurrentPhaseTerminable implements Terminable {
        @Override
        public void close() {
            if (currentPhase != null && !currentPhase.isClosed())
                currentPhase.closeAndReportException();
        }
    }
}
