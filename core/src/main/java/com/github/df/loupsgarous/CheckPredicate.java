package com.github.df.loupsgarous;

import java.util.function.Predicate;

public interface CheckPredicate<T> extends Predicate<T> {
    Check check(T t);

    @Override
    default boolean test(T t) {
        return check(t).isSuccess();
    }
}
