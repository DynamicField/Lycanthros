package com.github.jeuxjeux20.loupsgarous.util;

public final class CollectorUtils {
    private CollectorUtils() {
    }

    public static <A, B, R> R throwDuplicate(A a, B b) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot have duplicate entries: " + a + " and " + b + ".");
    }
}
