package com.github.jeuxjeux20.loupsgarous.game.tags.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTag;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTags;
import com.google.common.collect.ImmutableSet;

public class MaireTagRevealer implements TagRevealer {
    @Override
    public ImmutableSet<LGTag> getTagsRevealed(LGPlayer viewer, LGPlayer playerToReveal, LGGameOrchestrator orchestrator) {
        return ImmutableSet.of(LGTags.MAIRE);
    }
}
