package com.github.jeuxjeux20.loupsgarous;

import java.util.Optional;

/**
 * A simple interface to safe cast an object.
 * <p>
 * Think of it as a better {@code instanceof}.
 */
public interface SafeCast {
    default <T> Optional<T> safeCast(Class<T> clazz) {
        if (clazz.isInstance(this)) {
            return Optional.of(clazz.cast(this));
        }
        else {
            return Optional.empty();
        }
    }
}
