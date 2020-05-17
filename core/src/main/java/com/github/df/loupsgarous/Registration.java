package com.github.df.loupsgarous;

import me.lucko.helper.terminable.Terminable;

public interface Registration extends Terminable {
    void unregister();

    boolean isRegistered();

    @Override
    default void close() {
        unregister();
    }

    @Override
    default boolean isClosed() {
        return !isRegistered();
    }
}
