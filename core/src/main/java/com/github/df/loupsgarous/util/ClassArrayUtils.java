package com.github.df.loupsgarous.util;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.stream.Stream;

public final class ClassArrayUtils {
    private ClassArrayUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Class<?>> T[] merge(Stream<Collection<T>> lists) {
        Class<?>[] classes = lists.flatMap(Collection::stream).distinct().toArray(Class[]::new);
        return (T[]) classes;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Class<?>> T[] toArray(Collection<T> classes) {
        Class<?>[] array = classes.toArray(new Class[0]);
        return (T[]) array;
    }
}
