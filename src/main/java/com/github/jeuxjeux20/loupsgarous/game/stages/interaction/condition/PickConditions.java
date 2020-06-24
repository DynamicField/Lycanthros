package com.github.jeuxjeux20.loupsgarous.game.stages.interaction.condition;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.InvalidPickException;
import com.github.jeuxjeux20.loupsgarous.util.Check;

public interface PickConditions<T> {
    Check checkPicker(LGPlayer picker);

    Check checkTarget(T target);

    default Check checkPick(LGPlayer picker, T target) {
        return checkPicker(picker).and(() -> checkTarget(target));
    }

    /**
     * Throws an {@link InvalidPickException} if the given pick is invalid, using
     * {@link #checkPick(LGPlayer, Object)}.
     * @param picker the picker
     * @param target the target
     * @throws InvalidPickException when the pick is invalid
     */
    default void throwIfInvalid(LGPlayer picker, T target) {
        Check check = checkPick(picker, target);

        if (check.isError()) {
            throw new InvalidPickException(
                    "Invalid pick: " + check.getErrorMessage() +
                    " (picker: " + picker + ", target: " + target);
        }
    }
}
