package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.chat.ChatChannel;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatChannels;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.Check;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * The base class for any pick. It is highly recommended that you extend this class instead
 * of implementing {@link Pick}.
 *
 * @param <T> the type of the target
 */
public abstract class Pick<T> extends Interactable {
    protected final LGGameOrchestrator orchestrator;

    protected Pick(LGGameOrchestrator orchestrator) {
        super(orchestrator);
        this.orchestrator = orchestrator;
    }

    public final void pick(LGPlayer picker, T target) {
        Objects.requireNonNull(picker, "picker is null");
        Objects.requireNonNull(target, "target is null");

        conditions().throwIfInvalid(picker, target);

        safePick(picker, target);
    }

    public final PickConditions<T> conditions() {
        return conditionsBuilder()
                .use(new CriticalConditionsLoggingWrapper(allCriticalConditions()))
                .use(allPickConditions())
                .build();
    }

    private PickConditions<T> allCriticalConditions() {
        return conditionsBuilder()
                .ensurePicker(PickableConditions.checkPlayerGamePresence(orchestrator))
                .use(criticalConditions())
                .build();
    }

    private PickConditions<T> allPickConditions() {
        return conditionsBuilder()
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
     * EventActivator critical conditions are available in the {@link CriticalPickableConditions} class,
     * and classes such as {@link PlayerPick} already override this method.
     *
     * @return the critical conditions
     * @see CriticalPickableConditions
     */
    protected abstract PickConditions<T> criticalConditions();

    protected final FunctionalPickConditions.Builder<T> conditionsBuilder() {
        return PickConditions.builder();
    }

    protected abstract void safePick(LGPlayer picker, T target);

    public boolean isMyEvent(LGPickEvent event) {
        return event.getPickData().getSource() == this;
    }

    public ChatChannel getInfoMessagesChannel() {
        return LGChatChannels.createPickChannel(this);
    }

    public Stream<LGPlayer> getEligiblePickers() {
        return getOrchestrator().getPlayers().stream().filter(Check.predicate(conditions()::checkPicker));
    }

    public boolean canSomeonePick() {
        return getEligiblePickers().anyMatch(Check.predicate(conditions()::checkPicker));
    }

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
