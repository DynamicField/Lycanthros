package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.extensibility.ContentFactory;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.Sets;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.*;

public class PhaseCycle extends PhaseProgram {
    private List<ContentFactory<? extends RunnablePhase>> phases = new LinkedList<>();
    private ListIterator<ContentFactory<? extends RunnablePhase>> phaseIterator;

    private Disposable subscription = Disposable.disposed();

    public PhaseCycle(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    /**
     * Insert the given phase factory to the current game that will be created and run as soon as
     * possible (LIFO).
     *
     * @param phaseFactory the phase factory to insert
     */
    public void insert(ContentFactory<? extends RunnablePhase> phaseFactory) {
        phaseIterator.add(phaseFactory);
        phaseIterator.previous();
    }

    public void next() {
        if (phases.size() == 0 || !isRunning()) {
            return;
        }

        subscription.dispose();

        if (!phaseIterator.hasNext())
            phaseIterator = phases.listIterator(); // Reset the iterator

        ContentFactory<? extends RunnablePhase> factory = phaseIterator.next();
        RunnablePhase phase = factory.create(orchestrator);

        if (phase.getDescriptor().isTemporary())
            phaseIterator.remove();

        subscription = getPhaseRunner().run(new PhaseRunner.RunToken(phase, factory))
                .subscribe(r -> next(), e -> { /* TODO: Handle exceptions */ });
    }

    protected List<ContentFactory<? extends RunnablePhase>> getPhases() {
        return phases;
    }

    protected void setPhases(
            Collection<? extends ContentFactory<? extends RunnablePhase>> newPhases) {
        LinkedList<ContentFactory<? extends RunnablePhase>> newPhasesList =
                new LinkedList<>(newPhases);

        int index = determineNewIndex(newPhasesList);

        phases = newPhasesList;
        phaseIterator = newPhasesList.listIterator(index);

        if (!phases.isEmpty() && getPhaseRunner().getCurrent() != null &&
            !Objects.equals(phases.get(index), getPhaseRunner().getCurrent().getSource())) {
            next();
        }
    }

    @SuppressWarnings("unchecked")
    private int determineNewIndex(
            LinkedList<? extends ContentFactory<? extends RunnablePhase>> newPhases) {
        // The cycle's not running or we have no phases.
        if (phases.isEmpty() || getPhaseRunner().getCurrent() == null || newPhases.isEmpty()) {
            return 0;
        }

        // If the current phase is in the list, use it.
        ContentFactory<? extends RunnablePhase> runningFactory =
                (ContentFactory<? extends RunnablePhase>) getPhaseRunner().getCurrent().getSource();

        int newCurrentPhaseIndex = newPhases.indexOf(runningFactory);
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

        int oldCurrentPhaseIndex = phases.indexOf(runningFactory);
        Set<ContentFactory<? extends RunnablePhase>> oldPrecedingPhases = new HashSet<>();
        Set<ContentFactory<? extends RunnablePhase>> newPrecedingPhases = new HashSet<>();

        for (int i = 0; i < oldCurrentPhaseIndex; i++) {
            oldPrecedingPhases.add(phases.get(i));

            if (i < newPhases.size()) {
                newPrecedingPhases.add(newPhases.get(i));
            }
        }

        int elementsNotPreceding = Sets.difference(oldPrecedingPhases, newPrecedingPhases).size();

        return Math.min(oldCurrentPhaseIndex - elementsNotPreceding, newPhases.size() - 1);
    }

    @Override
    protected void startProgram() {
        next();
    }

    @Override
    protected void stopProgram() {
        subscription.dispose();
    }
}
