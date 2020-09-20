package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.extensibility.ContentFactory;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhaseDescriptor;
import com.github.jeuxjeux20.loupsgarous.util.FutureExceptionUtils;
import com.google.common.collect.Sets;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;
import java.util.logging.Level;

public class PhaseCycle implements Terminable {
    protected final LGGameOrchestrator orchestrator;

    private List<ContentFactory<? extends RunnableLGPhase>> phases = new LinkedList<>();
    private ListIterator<ContentFactory<? extends RunnableLGPhase>> phaseIterator;
    private @Nullable PhasePair runningPhase;

    private boolean running = false;

    public PhaseCycle(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    /**
     * Insert the given phase factory to the current game that will be created and run as soon as
     * possible (LIFO).
     *
     * @param phaseFactory the phase factory to insert
     */
    public void insert(ContentFactory<? extends RunnableLGPhase> phaseFactory) {
        phaseIterator.add(phaseFactory);
        phaseIterator.previous();
    }

    public void next() {
        if (phases.size() == 0 || !running) {
            updatePhase(null);
            return;
        }

        if (!phaseIterator.hasNext())
            phaseIterator = phases.listIterator(); // Reset the iterator

        ContentFactory<? extends RunnableLGPhase> factory = phaseIterator.next();
        RunnableLGPhase phase = factory.create(orchestrator);
        LGPhaseDescriptor descriptor = orchestrator.phases().descriptors().get(phase.getClass());

        if (descriptor.isTemporary())
            phaseIterator.remove();

        if (phase.shouldRun()) {
            runPhase(new PhasePair(phase, factory));
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
    public final LGPhase current() {
        return runningPhase == null ? new LGPhase.Null(orchestrator) : runningPhase.phase;
    }

    public final boolean isRunning() {
        return running;
    }

    void stop() {
        if (!running) {
            return;
        }

        running = false;
        updatePhase(null);
    }

    void start() {
        if (running) {
            return;
        }

        running = true;
        phaseIterator = phases.listIterator();
        next();
    }

    protected List<ContentFactory<? extends RunnableLGPhase>> getPhases() {
        return phases;
    }

    protected void setPhases(
            Collection<? extends ContentFactory<? extends RunnableLGPhase>> newPhases) {
        LinkedList<ContentFactory<? extends RunnableLGPhase>> newPhasesList =
                new LinkedList<>(newPhases);

        int index = determineNewIndex(newPhasesList);

        updatePhase(null);
        this.phases = newPhasesList;
        this.phaseIterator = newPhasesList.listIterator(index);
        next();
    }

    private int determineNewIndex(
            LinkedList<? extends ContentFactory<? extends RunnableLGPhase>> newPhases) {
        // The cycle's not running or we have no phases.
        if (phases.isEmpty() || runningPhase == null || newPhases.isEmpty()) {
            return 0;
        }

        // If the current phase is in the list, use it.
        int newCurrentPhaseIndex = newPhases.indexOf(runningPhase.factory);
        if (newCurrentPhaseIndex != -1) {
            return newCurrentPhaseIndex;
        }

        // Else, we'll have to find where the next phase *would* be.
        //
        // To do that, we try to find the items that are all present
        // before the old index, do the same for the new phases,
        // and move back for each old preceding item not present
        // in the new preceding items.
        //
        // This way, we can determine the new index that respects the old
        // succeeding phase, while ignoring succeeding candidates
        // that have been moved to the back.
        //
        // Okay, let's be honest, this explanation makes no sense; here's an
        // actual example:
        //
        // @ is the current phase
        // Old: A B C H @ E F G  (@index = 4)
        // New: E F G A H C
        //
        // Old preceding: A B C H
        // New preceding: E F G A
        //
        // Items in old that are not present in new: 3 (A, B and H)
        // @index - 3 = 1
        //
        // The new index is 1, so the next phase will be F:
        //     v
        // = E F G A H C
        //
        // And why isn't it E? Because it has been moved into the back!
        // Hope you understood this stupid algorithm :D

        int oldCurrentPhaseIndex = phases.indexOf(runningPhase.factory);
        Set<ContentFactory<? extends RunnableLGPhase>> oldPrecedingPhases = new HashSet<>();
        Set<ContentFactory<? extends RunnableLGPhase>> newPrecedingPhases = new HashSet<>();

        for (int i = 0; i < oldCurrentPhaseIndex; i++) {
            oldPrecedingPhases.add(phases.get(i));

            if (i < newPhases.size()) {
                newPrecedingPhases.add(newPhases.get(i));
            }
        }

        int elementsNotPreceding = Sets.difference(oldPrecedingPhases, newPrecedingPhases).size();

        return Math.min(oldCurrentPhaseIndex - elementsNotPreceding, newPhases.size() - 1);
    }

    protected void runPhase(PhasePair phasePair) {
        updatePhase(phasePair);

        phasePair.phase.run()
                .exceptionally(this::handlePhaseException)
                .thenRun(() -> {
                    if (phasePair.runsNext) {
                        next();
                    }
                })
                .exceptionally(this::handlePostPhaseException);
    }

    private void updatePhase(@Nullable PhasePair newPhasePair) {
        if (runningPhase != null) {
            runningPhase.dispose();
        }

        runningPhase = newPhasePair;
    }

    private Void handlePhaseException(Throwable ex) {
        if (FutureExceptionUtils.isCancellation(ex)) {
            // That's cancelled, rethrow to avoid executing further actions.

            throw FutureExceptionUtils.asCompletionException(ex);
        } else {
            // If something wrong happens, let's just log and continue.
            // We still want the game to continue!

            orchestrator.logger().log(Level.SEVERE, "Unhandled exception while running phase: " + runningPhase, ex);
            return null;
        }
    }

    private Void handlePostPhaseException(Throwable ex) {
        if (!FutureExceptionUtils.isCancellation(ex)) {
            orchestrator.logger().log(Level.SEVERE, "Unhandled exception while the game was running the next phase.", ex);
        }
        // TODO: What do we do here?
        return null;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void close() throws Exception {
        if (runningPhase != null) {
            runningPhase.dispose();
        }
    }

    protected static final class PhasePair {
        final RunnableLGPhase phase;
        final ContentFactory<? extends RunnableLGPhase> factory;
        boolean runsNext = true;

        public PhasePair(RunnableLGPhase phase, ContentFactory<? extends RunnableLGPhase> factory) {
            this.phase = phase;
            this.factory = factory;
        }

        public void dispose() {
            runsNext = false;
            if (phase != null && !phase.isClosed()) {
                phase.closeAndReportException();
            }
        }
    }
}
