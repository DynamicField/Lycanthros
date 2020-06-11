package com.github.jeuxjeux20.loupsgarous.game.atmosphere;

import me.lucko.helper.terminable.Terminable;

public interface Structure extends Terminable {
    void build();
    void remove();

    @Override
    default void close() {
        remove();
    }
}
