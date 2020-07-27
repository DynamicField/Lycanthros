package com.github.jeuxjeux20.loupsgarous.tags.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.tags.LGTag;
import com.github.jeuxjeux20.loupsgarous.tags.LGTags;
import com.google.common.collect.ImmutableSet;

public class MaireTagRevealer implements TagRevealer {
    @Override
    public ImmutableSet<LGTag> getTagsRevealed(LGPlayer viewer, LGPlayer playerToReveal, LGGame game) {
        return ImmutableSet.of(LGTags.MAIRE);
    }
}
