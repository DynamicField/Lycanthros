package com.github.df.loupsgarous.util;

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

}
