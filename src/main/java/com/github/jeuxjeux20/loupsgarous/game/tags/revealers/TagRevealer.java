package com.github.jeuxjeux20.loupsgarous.game.tags.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTag;
import com.google.common.collect.ImmutableSet;

public interface TagRevealer {
    ImmutableSet<LGTag> getTagsRevealed(LGPlayer viewer, LGPlayer playerToReveal, LGGameOrchestrator orchestrator);
}
