package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.SnapshotComposition;

/**
 * A set of data to create a new Loups-Garous game instance.
 */
public final class LGGameLobbyInfo {
    private final String id;
    private final Composition composition;

    public LGGameLobbyInfo(Composition composition, String id) {
        this.composition = new SnapshotComposition(composition);
        this.id = id;
    }



    public String getId() {
        return id;
    }

    public Composition getComposition() {
        return composition;
    }
}
