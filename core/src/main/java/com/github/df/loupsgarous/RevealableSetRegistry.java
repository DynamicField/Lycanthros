package com.github.df.loupsgarous;

import com.github.df.loupsgarous.game.LGPlayer;
import com.google.common.collect.ImmutableSet;

public interface RevealableSetRegistry<T> extends SetRegistry<T> {
    default ImmutableSet<T> getVisibleFor(LGPlayer viewer) {
        return get().stream()
                .filter(x -> isRevealed(x, viewer))
                .collect(ImmutableSet.toImmutableSet());
    }

    boolean isRevealed(T item, LGPlayer viewer);
}
