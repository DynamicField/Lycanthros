package com.github.jeuxjeux20.loupsgarous.util;

import java.util.Optional;
import java.util.stream.Stream;

public final class OptionalUtils {
    private OptionalUtils() {
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Stream<T> stream(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }
}
