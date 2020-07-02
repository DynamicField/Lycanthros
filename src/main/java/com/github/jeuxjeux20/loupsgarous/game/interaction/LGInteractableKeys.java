package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.Votable;

import static com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableTypes.*;

public final class LGInteractableKeys {
    public static final InteractableKey<Pickable<LGPlayer>> HEAL = new InteractableKey<>(Names.HEAL, PLAYER_PICKABLE);
    public static final InteractableKey<Pickable<LGPlayer>> KILL = new InteractableKey<>(Names.KILL, PLAYER_PICKABLE);
    public static final InteractableKey<Pickable<LGPlayer>> LOOK = new InteractableKey<>(Names.LOOK, PLAYER_PICKABLE);
    public static final InteractableKey<Pickable<Couple>> COUPLE_CREATOR = new InteractableKey<>(Names.COUPLE_CREATOR, COUPLE_PICKABLE);
    public static final InteractableKey<Votable<LGPlayer>> PLAYER_VOTE = new InteractableKey<>(Names.PLAYER_VOTE, PLAYER_VOTABLE);

    private LGInteractableKeys() {
    }

    public static final class Names {
        public static final String HEAL = "Heal";
        public static final String KILL = "Kill";
        public static final String LOOK = "Look";
        public static final String COUPLE_CREATOR = "CoupleCreator";
        public static final String PLAYER_VOTE = "PlayerVote";
    }
}
