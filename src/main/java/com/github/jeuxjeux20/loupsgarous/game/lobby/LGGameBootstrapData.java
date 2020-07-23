package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.ImmutableComposition;
import org.bukkit.entity.Player;

/**
 * A set of data to create a new Loups-Garous game instance.
 */
public final class LGGameBootstrapData {
    private final Player owner;
    private final String id;
    private final Composition composition;

    public LGGameBootstrapData(Player owner, Composition composition, String id) {
        this.owner = owner;
        this.composition = new ImmutableComposition(composition);
        this.id = id;
    }

    public Player getOwner() {
        return owner;
    }

    public Composition getComposition() {
        return composition;
    }

    public String getId() {
        return id;
    }
}
