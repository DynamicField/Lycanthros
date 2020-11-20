package com.github.jeuxjeux20.loupsgarous.lobby;

import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.cards.composition.ImmutableComposition;

/**
 * A set of data to create a new Loups-Garous game instance.
 */
public final class LGGameBootstrapData {
    private final String id;
    private final Composition composition;

    public LGGameBootstrapData(Composition composition, String id) {
        this.composition = new ImmutableComposition(composition);
        this.id = id;
    }

    public Composition getComposition() {
        return composition;
    }

    public String getId() {
        return id;
    }
}
