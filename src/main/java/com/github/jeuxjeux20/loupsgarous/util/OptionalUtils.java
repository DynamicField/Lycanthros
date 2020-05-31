package com.github.jeuxjeux20.loupsgarous.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class OptionalUtils {
    private OptionalUtils() {
    }

    public static <T> Stream<T> stream(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    @SafeVarargs
    public static <T> Optional<T> or(Supplier<Optional<T>>... optionalSuppliers) {
        return Arrays.stream(optionalSuppliers)
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .findFirst()
                .orElseGet(Optional::empty);
    }

    @SuppressWarnings("RedundantThrows") // It throws but sneakily.
    public static <T, R, E extends Throwable>
    Optional<R> mapThrows(Optional<? extends T> optional,
                          ThrowingFunction<? super T, ? extends R, ? extends E> function) throws E {
        return optional.map(function);
    }
}
