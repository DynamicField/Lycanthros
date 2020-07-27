package com.github.jeuxjeux20.loupsgarous.tags.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.tags.LGTag;
import com.google.common.collect.ImmutableSet;

public interface TagRevealer {
    ImmutableSet<LGTag> getTagsRevealed(LGPlayer viewer, LGPlayer playerToReveal, LGGame game);
}
