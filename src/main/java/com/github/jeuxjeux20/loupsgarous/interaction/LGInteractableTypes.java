package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.Vote;
import com.google.common.reflect.TypeToken;

public final class LGInteractableTypes {
    public static final TypeToken<Pick<LGPlayer>> PLAYER_PICKABLE = new TypeToken<Pick<LGPlayer>>() {};
    public static final TypeToken<Pick<Couple>> COUPLE_PICKABLE = new TypeToken<Pick<Couple>>() {};
    public static final TypeToken<Vote<LGPlayer>> PLAYER_VOTABLE = new TypeToken<Vote<LGPlayer>>() {};
}
