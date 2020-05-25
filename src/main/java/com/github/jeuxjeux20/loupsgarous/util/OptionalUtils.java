package com.github.jeuxjeux20.loupsgarous.util;

import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class OptionalUtils {
    private OptionalUtils() {
    }

    public static <T> Stream<T> stream(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    @SuppressWarnings("RedundantThrows") // It throws but sneakily.
    public static <T, R, E extends Throwable>
    Optional<R> mapThrows(Optional<? extends T> optional,
                          ThrowingFunction<? super T, ? extends R, ? extends E> function) throws E {
        return optional.map(function);
    }
}
