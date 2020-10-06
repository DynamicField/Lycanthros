package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.util.FutureExceptionUtils;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class PhaseRunner {
    private @Nullable RunToken current;
    private final LGGameOrchestrator orchestrator;

    PhaseRunner(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public @Nullable RunToken getCurrent() {
        return current;
    }

    public Single<PhaseResult> run(RunnableLGPhase phase) {
        return run(new RunToken(phase, null));
    }

    public Single<PhaseResult> run(RunnableLGPhase phase, Object source) {
        return run(new RunToken(phase, source));
    }

    public Single<PhaseResult> run(RunToken token) {
        if (token.getPhase().getOrchestrator() != orchestrator) {
            throw new IllegalArgumentException(
                    "The given RunToken phase has been ran on the wrong PhaseRunner."
            );
        }

        RunnableLGPhase phase = token.getPhase();

        if (!phase.shouldRun()) {
            phase.closeAndReportException();
            return Single.just(new PhaseResult(token, PhaseTerminationMethod.NOT_RAN));
        }

        terminateCurrent();
        this.current = token;
        CompletableFuture<Void> phaseTask = phase.run();

        return Single.create(subscriber -> phaseTask.whenComplete((r, e) -> {
            if (this.current == token) {
                this.current = null;
            }

            PhaseResult result = null;

            if (FutureExceptionUtils.isCancellation(e)) {
                result = new PhaseResult(token, PhaseTerminationMethod.CANCELLED);
            } else if (e == null) {
                result = new PhaseResult(token, PhaseTerminationMethod.NORMAL);
            }

            if (result != null) {
                subscriber.onSuccess(result);
            } else {
                orchestrator.logger().log(
                        Level.WARNING, "Phase " + phase + " failed to execute.", e);
                subscriber.onError(e);
            }
        }));
    }

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    public void terminateCurrent() {
        if (current != null) {
            current.getPhase().closeAndReportException();
            current = null;
        }
    }

    public static final class RunToken {
        private final RunnableLGPhase phase;
        private final Object source;

        public RunToken(RunnableLGPhase phase, Object source) {
            this.phase = phase;
            this.source = source;
        }

        public RunnableLGPhase getPhase() {
            return phase;
        }

        public Object getSource() {
            return source;
        }
    }
}
