package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import static com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableTypes.*;

public final class LGInteractableKeys {
    public static final InteractableKey<Pickable<LGPlayer>> HEAL = new InteractableKey<>("Heal", PLAYER_PICKABLE);
    public static final InteractableKey<Pickable<LGPlayer>> KILL = new InteractableKey<>("Kill", PLAYER_PICKABLE);
    public static final InteractableKey<Pickable<LGPlayer>> LOOK = new InteractableKey<>("Look", PLAYER_PICKABLE);
    public static final InteractableKey<Pickable<Couple>> COUPLE_CREATOR = new InteractableKey<>("CoupleCreator", COUPLE_PICKABLE);
    public static final InteractableKey<Votable<LGPlayer>> PLAYER_VOTE = new InteractableKey<>("PlayerVote", PLAYER_VOTABLE);

    private LGInteractableKeys() {
    }

}
