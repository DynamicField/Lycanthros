package com.github.df.loupsgarous.interaction.condition;

import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.InvalidPickException;
import com.github.df.loupsgarous.interaction.PickData;
import com.github.df.loupsgarous.Check;

import java.util.function.Function;

public interface PickConditions<T> {
    PickConditions<?> EMPTY = new Empty();

    Check checkPicker(LGPlayer picker);

    Check checkTarget(T target);

    Check checkPick(LGPlayer picker, T target);

    default Check checkPick(PickData<T> pickData) {
        return checkPick(pickData.getPicker(), pickData.getTarget());
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

    default <R> PickConditions<R> map(Function<? super R, ? extends T> mappingFunction) {
        PickConditions<T> self = this;

        return new PickConditions<R>() {
            @Override
            public Check checkPicker(LGPlayer picker) {
                return self.checkPicker(picker);
            }

            @Override
            public Check checkTarget(R target) {
                return self.checkTarget(mappingFunction.apply(target));
            }

            @Override
            public Check checkPick(LGPlayer picker, R target) {
                return self.checkPick(picker, mappingFunction.apply(target));
            }
        };
    }

    @SuppressWarnings("unchecked")
    static <T> PickConditions<T> empty() {
        return (PickConditions<T>) EMPTY;
    }

    static <T> FunctionalPickConditions.Builder<T> builder() {
        return FunctionalPickConditions.builder();
    }

    class Empty implements PickConditions<Object> {
        @Override
        public Check checkPicker(LGPlayer picker) {
            return Check.success();
        }

        @Override
        public Check checkTarget(Object target) {
            return Check.success();
        }

        @Override
        public Check checkPick(LGPlayer picker, Object target) {
            return Check.success();
        }
    }
}
