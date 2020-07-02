package com.github.jeuxjeux20.loupsgarous.game.tags.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTag;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import java.util.Set;

class TagRevealerAggregator implements TagRevealer {
    private final Set<TagRevealer> tagRevealers;

    @Inject
    TagRevealerAggregator(Set<TagRevealer> tagRevealers) {
        this.tagRevealers = tagRevealers;
    }

    @Override
    public ImmutableSet<LGTag> getTagsRevealed(LGPlayer viewer, LGPlayer playerToReveal, LGGameOrchestrator orchestrator) {
        return tagRevealers.stream()
                .flatMap(x -> x.getTagsRevealed(viewer, playerToReveal, orchestrator).stream())
                .collect(ImmutableSet.toImmutableSet());
    }
}
