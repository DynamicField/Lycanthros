package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.Votable;
import com.google.common.reflect.TypeToken;

public final class LGInteractableTypes {
    public static final TypeToken<Pickable<LGPlayer>> PLAYER_PICKABLE = new TypeToken<Pickable<LGPlayer>>() {};
    public static final TypeToken<Pickable<Couple>> COUPLE_PICKABLE = new TypeToken<Pickable<Couple>>() {};
    public static final TypeToken<Votable<LGPlayer>> PLAYER_VOTABLE = new TypeToken<Votable<LGPlayer>>() {};
}
