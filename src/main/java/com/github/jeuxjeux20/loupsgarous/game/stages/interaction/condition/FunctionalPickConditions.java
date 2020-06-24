package com.github.jeuxjeux20.loupsgarous.game.stages.interaction.condition;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.function.*;

public final class FunctionalPickConditions<T> implements PickConditions<T> {
    private final ImmutableList<PickerPredicate> pickerPredicates;
    private final ImmutableList<TargetPredicate<? super T>> targetPredicates;
    private final ImmutableList<PickPredicate<? super T>> pickPredicates;

    public FunctionalPickConditions(Collection<PickerPredicate> pickerPredicates,
                                    Collection<TargetPredicate<? super T>> targetPredicates,
                                    Collection<PickPredicate<? super T>> pickPredicates) {
        this.pickerPredicates = ImmutableList.copyOf(pickerPredicates);
        this.targetPredicates = ImmutableList.copyOf(targetPredicates);
        this.pickPredicates = ImmutableList.copyOf(pickPredicates);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> Builder<T> builder(PickConditions<? super T> conditions) {
        return new Builder<>(conditions);
    }

    @Override
    public Check checkPicker(LGPlayer picker) {
        return aggregateChecks(pickerPredicates, p -> p.test(picker));
    }

    @Override
    public Check checkTarget(T target) {
        return aggregateChecks(targetPredicates, p -> p.test(target));
    }

    @Override
    public Check checkPick(LGPlayer picker, T target) {
        Check baseCheck = checkPicker(picker).and(() -> checkTarget(target));

        if (baseCheck.isError()) {
            return baseCheck;
        } else {
            return aggregateChecks(pickPredicates, p -> p.test(picker, target));
        }
    }

    private <I> Check aggregateChecks(Iterable<? extends I> items, Function<I, Check> itemCheckFunction) {
        for (I item : items) {
            Check check = itemCheckFunction.apply(item);

            if (check.isError()) {
                return check;
            }
        }
        return Check.success();
    }

    public interface PickerPredicate {
        Check test(LGPlayer picker);

        interface BoolPredicate extends Predicate<LGPlayer> {}
    }

    public interface TargetPredicate<T> {
        Check test(T target);

        interface BoolPredicate<T> extends Predicate<T> {}
    }

    public interface PickPredicate<T> {
        Check test(LGPlayer picker, T target);

        interface BoolPredicate<T> extends BiPredicate<LGPlayer, T> {}
    }

    public static final class Builder<T> {
        private final ImmutableList.Builder<PickerPredicate> pickerPredicates = ImmutableList.builder();
        private final ImmutableList.Builder<TargetPredicate<? super T>> targetPredicates = ImmutableList.builder();
        private final ImmutableList.Builder<PickPredicate<? super T>> pickPredicates = ImmutableList.builder();

        public Builder() {
        }

        @SuppressWarnings("unchecked")
        public Builder(PickConditions<? super T> conditions) {
            if (conditions instanceof FunctionalPickConditions<?>) {
                FunctionalPickConditions<? super T> functionalPickConditions =
                        (FunctionalPickConditions<? super T>) conditions;

                this.pickerPredicates.addAll(functionalPickConditions.pickerPredicates);
                this.targetPredicates.addAll(functionalPickConditions.targetPredicates);
                this.pickPredicates.addAll(functionalPickConditions.pickPredicates);
            } else {
                this.pickerPredicates.add(conditions::checkPicker);
                this.targetPredicates.add(conditions::checkTarget);
                this.pickPredicates.add(conditions::checkPick);
            }
        }

        public Builder<T> ensurePicker(PickerPredicate.BoolPredicate predicate, String message) {
            pickerPredicates.add(p -> Check.ensure(predicate.test(p), message));
            return this;
        }

        public Builder<T> ensurePicker(PickerPredicate.BoolPredicate predicate,
                                       Function<? super LGPlayer, String> messageFunction) {
            pickerPredicates.add(p -> Check.ensure(predicate.test(p), messageFunction.apply(p)));
            return this;
        }

        public Builder<T> ensurePicker(PickerPredicate checkPredicate) {
            pickerPredicates.add(checkPredicate);
            return this;
        }

        public Builder<T> ensureTarget(TargetPredicate.BoolPredicate<T> predicate, String message) {
            targetPredicates.add(t -> Check.ensure(predicate.test(t), message));
            return this;
        }

        public Builder<T> ensureTarget(TargetPredicate.BoolPredicate<T> predicate,
                                       Function<? super T, String> messageFunction) {
            targetPredicates.add(t -> Check.ensure(predicate.test(t), messageFunction.apply(t)));
            return this;
        }

        public Builder<T> ensureTarget(TargetPredicate<T> checkPredicate) {
            targetPredicates.add(checkPredicate);
            return this;
        }

        public Builder<T> ensurePick(PickPredicate.BoolPredicate<T> predicate, String message) {
            pickPredicates.add((p, t) -> Check.ensure(predicate.test(p, t), message));
            return this;
        }

        public Builder<T> ensurePick(PickPredicate.BoolPredicate<T> predicate,
                                     BiFunction<? super LGPlayer, ? super T, String> messageFunction) {
            pickPredicates.add((p, t) -> Check.ensure(predicate.test(p, t), messageFunction.apply(p, t)));
            return this;
        }

        public Builder<T> ensurePick(PickPredicate<T> checkPredicate) {
            pickPredicates.add(checkPredicate);
            return this;
        }

        public Builder<T> apply(Consumer<? super Builder<T>> consumer) {
            consumer.accept(this);
            return this;
        }

        public FunctionalPickConditions<T> build() {
            return new FunctionalPickConditions<>(pickerPredicates.build(), targetPredicates.build(), pickPredicates.build());
        }
    }
}
