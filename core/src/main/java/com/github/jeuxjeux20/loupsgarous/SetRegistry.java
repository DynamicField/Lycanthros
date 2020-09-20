package com.github.jeuxjeux20.loupsgarous;

import com.google.common.collect.ImmutableSet;

public interface SetRegistry<T> {
    ImmutableSet<T> get();

    boolean add(T item);

    boolean has(T item);

    boolean remove(T item);
}
