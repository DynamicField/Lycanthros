package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.util.Check;

import java.util.Objects;

/**
 * The base class for any pick. It is highly recommended that you extend this class instead
 * of implementing {@link Pick}.
 *
 * @param <T> the type of the target
 */
public abstract class AbstractPick<T> extends AbstractInteractable implements Pick<T> {
    protected final LGGameOrchestrator orchestrator;

    protected AbstractPick(LGGameOrchestrator orchestrator) {
        super(orchestrator);
        this.orchestrator = orchestrator;
    }

    @Override
    public final void pick(LGPlayer picker, T target) {
        throwIfClosed();

        Objects.requireNonNull(picker, "picker is null");
        Objects.requireNonNull(target, "target is null");

        conditions().throwIfInvalid(picker, target);

        safePick(picker, target);
    }

    @Override
    public final PickConditions<T> conditions() {
        return FunctionalPickConditions.<T>builder()
                .use(new CriticalConditionsLoggingWrapper(allCriticalConditions()))
                .use(allPickConditions())
                .build();
    }

    private PickConditions<T> allCriticalConditions() {
        return FunctionalPickConditions.<T>builder()
                .ensurePicker(PickableConditions.checkPlayerGamePresence(orchestrator))
                .use(criticalConditions())
                .build();
    }

    private PickConditions<T> allPickConditions() {
        return FunctionalPickConditions.<T>builder()
                .ensurePicker(LGPlayer::isPresent, "Vous n'êtes pas dans la partie.")
                .use(pickConditions())
                .build();
    }

    /**
     * Defines the pick conditions used to check any error that may arise
     * from a user error. They are evaluated after {@link #criticalConditions()}.
     * <p>
     * Various pick conditions are available in the {@link PickableConditions} class.
     *
     * @return the pick conditions
     * @see PickableConditions
     */
    protected abstract PickConditions<T> pickConditions();

    /**
     * Defines the critical conditions used to check any error that may arise from a
     * programming error. If a critical condition evaluates as an error, it is logged
     * as a warning.
     * <p>
     * In addition to this method, the picker is checked for
     * its presence in the game's players and
     * not being away ({@link LGPlayer#isAway()}).
     * <p>
     * Default critical conditions are available in the {@link CriticalPickableConditions} class,
     * and classes such as {@link AbstractPlayerPick} already override this method.
     *
     * @return the critical conditions
     * @see CriticalPickableConditions
     */
    protected abstract PickConditions<T> criticalConditions();

    protected abstract void safePick(LGPlayer picker, T target);

    private final class CriticalConditionsLoggingWrapper implements PickConditions<T> {
        private final PickConditions<T> pickConditions;

        CriticalConditionsLoggingWrapper(PickConditions<T> pickConditions) {
            this.pickConditions = pickConditions;
        }

        @Override
        public Check checkPicker(LGPlayer picker) {
            return logError(pickConditions.checkPicker(picker));
        }

        @Override
        public Check checkTarget(T target) {
            return logError(pickConditions.checkTarget(target));
        }

        @Override
        public Check checkPick(LGPlayer picker, T target) {
            return logError(pickConditions.checkPick(picker, target));
        }

        private Check logError(Check check) {
            if (check.isError()) {
                orchestrator.logger().warning("Critical condition failed: " + check + ".");
            }
            return check;
        }
    }
}
