package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.base.Preconditions;
import io.reactivex.rxjava3.disposables.Disposable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class StateTransitionHandler {
    private final MinecraftLGGameOrchestrator orchestrator;
    private final Map<StateTransitionBlocker, Disposable> blockers = new HashMap<>();
    private boolean blocked = false;
    private @Nullable StateTransition currentTransition;

    StateTransitionHandler(MinecraftLGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public void addBlocker(StateTransitionBlocker blocker) {
        if (!blockers.containsKey(blocker)) {
            Disposable subscription
                    = blocker.blockingUpdates().subscribe(v -> updateBlocked());

            blockers.put(blocker, subscription);
            updateBlocked();
        }
    }

    public void removeBlocker(StateTransitionBlocker blocker) {
        Disposable subscription = blockers.remove(blocker);
        if (subscription != null) {
            subscription.dispose();
            updateBlocked();
        }
    }

    public boolean requestExecutionOverride(StateTransition transition) {
        return doRequestExecution(transition, true);
    }

    public boolean requestExecution(StateTransition transition) {
        return doRequestExecution(transition, false);
    }

    private boolean doRequestExecution(StateTransition transition, boolean overridePending) {
        Preconditions.checkArgument(transition.getState() == StateTransition.State.READY,
                "The given state transition must be ready.");

        if (currentTransition != null) {
            if (currentTransition.isEnforced()) {
                String reason = String.format(
                        "Current state transition (%s) is enforced", currentTransition);
                logExecutionResult(transition, false, reason);
                return false;
            }

            if (overridePending && currentTransition.getState() == StateTransition.State.PENDING) {
                orchestrator.logger().fine(String.format(
                        "Cancelling state transition %s in favor of %s",
                        currentTransition, transition));
                currentTransition.setState(StateTransition.State.CANCELLED);
            } else {
                String reason = String.format(
                        "Current state transition (%s) is already running", currentTransition);
                logExecutionResult(transition, false, reason);
                return false;
            }
        }

        logExecutionResult(transition, true, "attempting to run " + (blocked ? "later" : "now"));
        currentTransition = transition;
        if (!blocked) {
            run(transition);
        } else {
            transition.setState(StateTransition.State.PENDING);
        }

        return true;
    }

    private void logExecutionResult(StateTransition transition,
            boolean result, String reason) {
        String message = String.format(
                "State transition %s has been %s: %s",
                transition, result ? "accepted" : "rejected", reason);
        orchestrator.logger().fine(message);
    }

    private void run(StateTransition transition) {
        Preconditions.checkState(!blocked, "Cannot run a state transition while blocked.");

        transition.setState(StateTransition.State.RUNNING);
        try {
            orchestrator.logger().fine(
                    String.format("Running state transition %s", transition));

            orchestrator.dispatchStateTransition(transition);
            transition.setState(StateTransition.State.COMPLETED);

            orchestrator.logger().fine(
                    String.format("State transition %s ran successfully", transition));
        } catch (Throwable e) {
            String message = String.format(
                    "Failed running state transition %s from state %s",
                    transition, orchestrator.getState());
            orchestrator.logger().log(Level.SEVERE, message, e);

            transition.setState(StateTransition.State.ERROR);
            transition.setError(e);
        }
        currentTransition = null;
    }

    public @Nullable StateTransition getCurrentTransition() {
        return currentTransition;
    }

    public boolean isBlocked() {
        return blocked;
    }

    private void setBlocked(boolean blocked) {
        if (this.blocked == blocked) {
            return;
        }

        this.blocked = blocked;
        if (!blocked && currentTransition != null &&
            currentTransition.getState() == StateTransition.State.PENDING) {
            orchestrator.logger().finer(
                    "State transitions have been unblocked, now running " +
                    "the pending transition.");
            run(currentTransition);
        }
    }

    private void updateBlocked() {
        for (StateTransitionBlocker blocker : blockers.keySet()) {
            if (blocker.isBlocking()) {
                setBlocked(true);
                return;
            }
        }

        setBlocked(false);
    }
}
