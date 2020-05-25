package com.github.jeuxjeux20.loupsgarous.util;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.IllegalPlayerCountException;

import java.util.function.Function;

public interface ThrowingFunction<T, R, E extends Throwable> extends Function<T, R> {
    R applyThrowing(T t) throws E, IllegalPlayerCountException;

    @Override
    default R apply(T t) {
        try {
            return applyThrowing(t);
        } catch (Throwable e) {
            throw sneakyThrow(e);
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable> RuntimeException sneakyThrow(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
