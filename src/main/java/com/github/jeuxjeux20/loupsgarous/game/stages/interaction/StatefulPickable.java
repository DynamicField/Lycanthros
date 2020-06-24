package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

public interface StatefulPickable<T> extends Pickable<T> {
    ImmutableMap<LGPlayer, T> getPicks();

    void removePick(@NotNull LGPlayer picker);

    default void togglePick(LGPlayer picker, T target) {
        if (getPicks().get(picker) == target) {
            removePick(picker);
        } else {
            pick(picker, target);
        }
    }

    default boolean hasPick(LGPlayer picker) {
        return getPicks().containsKey(picker);
    }
}
