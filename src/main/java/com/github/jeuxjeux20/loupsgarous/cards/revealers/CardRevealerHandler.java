package com.github.jeuxjeux20.loupsgarous.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.extensibility.ExtensionPointHandler;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.CARD_REVEALERS;

public final class CardRevealerHandler implements ExtensionPointHandler {
    private final LGGameOrchestrator orchestrator;

    @Inject
    CardRevealerHandler(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public boolean willReveal(LGPlayer viewer, LGPlayer playerToReveal) {
        for (CardRevealer cardRevealer : orchestrator.bundle().contents(CARD_REVEALERS)) {
            if (cardRevealer.willReveal(viewer, playerToReveal, orchestrator)) {
                return true;
            }
        }
        return false;
    }
}
