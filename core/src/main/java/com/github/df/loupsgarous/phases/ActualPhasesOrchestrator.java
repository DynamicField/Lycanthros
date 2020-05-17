package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.StateTransitionBlocker;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ActualPhasesOrchestrator implements PhasesOrchestrator, Terminable {
    private final PhaseRunner phaseRunner;

    private @Nullable PhaseProgram program;
    private final LGGameOrchestrator orchestrator;
    private final ReplaceablePhaseStateTransitionBlocker transitionBlocker;

    private final Phase.Null nullPhase;

    public ActualPhasesOrchestrator(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
        this.nullPhase = new Phase.Null(orchestrator);
        this.phaseRunner = new PhaseRunner(orchestrator);
        this.program = new EmptyPhaseProgram(orchestrator);

        transitionBlocker = new ReplaceablePhaseStateTransitionBlocker();
        orchestrator.stateTransitions().addBlocker(transitionBlocker);
    }

    @Override
    public Phase current() {
        PhaseRunner.RunToken currentToken = phaseRunner.getCurrent();
        return wrapNullPhase(currentToken);
    }

    @Override
    public Observable<Phase> currentUpdates() {
        return phaseRunner.currentUpdates()
                .map(o -> wrapNullPhase(o.orElse(null)));
    }

    private Phase wrapNullPhase(PhaseRunner.RunToken token) {
        return token == null ? nullPhase : token.getPhase();
    }

    @Override
    public @Nullable PhaseProgram getProgram() {
        return program;
    }

    @Override
    public void startProgram(PhaseProgram program) {
        orchestrator.getState().mustBeActive();

        if (program == null || this.program == program || program.isRunning()) {
            return;
        }

        stopProgram(this.program);
        this.program = program;
        program.setRunning(true);
    }

    @Override
    public void stopProgram(PhaseProgram program) {
        if (program == null || this.program != program || !program.isRunning()) {
            return;
        }

        this.program = null;
        program.setRunning(false);
        try {
            getPhaseRunner().terminateCurrent();
        } catch (PhaseTransitioningException e) {
            orchestrator.logger().warning("Program ended while transitioning");
        }
    }

    PhaseRunner getPhaseRunner() {
        return phaseRunner;
    }

    @Override
    public void close() {
        transitionBlocker.close();
        orchestrator.stateTransitions().removeBlocker(transitionBlocker);
        stopProgram(program);
    }

    private class ReplaceablePhaseStateTransitionBlocker
            implements StateTransitionBlocker, Terminable {
        private final Disposable subscription;
        private final PublishSubject<Boolean> blockingSubject = PublishSubject.create();
        private boolean isBlocking;

        ReplaceablePhaseStateTransitionBlocker() {
            subscription = phaseRunner.currentUpdates()
                    .map(opt -> opt.map(x -> x.getPhase().stateChanges()))
                    .switchMap(opt -> opt
                            .map(r -> r.map(Optional::of))
                            .orElse(Observable.just(Optional.empty())))
                    .subscribe(state -> {
                        boolean replaceable = state
                                .map(Phase.State::isReplaceable)
                                .orElse(true);
                        setBlocking(!replaceable);
                    });
        }

        @Override
        public Observable<Boolean> blockingUpdates() {
            return blockingSubject;
        }

        @Override
        public boolean isBlocking() {
            return isBlocking;
        }

        private void setBlocking(boolean blocking) {
            if (this.isBlocking == blocking) {
                return;
            }

            this.isBlocking = blocking;
            blockingSubject.onNext(blocking);
        }

        @Override
        public void close() {
            subscription.dispose();
        }
    }
}
