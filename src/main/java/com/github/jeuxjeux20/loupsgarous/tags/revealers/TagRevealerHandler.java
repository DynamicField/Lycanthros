package com.github.jeuxjeux20.loupsgarous.tags.revealers;

import com.github.jeuxjeux20.loupsgarous.extensibility.ExtensionPointHandler;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.tags.LGTag;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.TAG_REVEALERS;

public class TagRevealerHandler implements ExtensionPointHandler {
    private final LGGameOrchestrator orchestrator;

    @Inject
    TagRevealerHandler(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public ImmutableSet<LGTag> getTagsRevealed(LGPlayer viewer, LGPlayer playerToReveal) {
        return orchestrator.getGameBox().contents(TAG_REVEALERS).stream()
                .flatMap(x -> x.getTagsRevealed(viewer, playerToReveal, orchestrator).stream())
                .filter(playerToReveal.tags()::has)
                .collect(ImmutableSet.toImmutableSet());
    }
}
