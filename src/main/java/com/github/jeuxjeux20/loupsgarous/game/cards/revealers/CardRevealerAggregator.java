package com.github.jeuxjeux20.loupsgarous.game.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;

import java.util.Set;

public final class CardRevealerAggregator implements CardRevealer {
    private final Set<CardRevealer> cardRevealers;

    @Inject
    CardRevealerAggregator(Set<CardRevealer> cardRevealers) {
        this.cardRevealers = cardRevealers;
    }

    @Override
    public boolean willReveal(LGPlayer playerToReveal, LGPlayer target, LGGameOrchestrator orchestrator) {
        for (CardRevealer cardRevealer : cardRevealers) {
            if (cardRevealer.willReveal(playerToReveal, target, orchestrator)) {
                return true;
            }
        }
        return false;
    }
}
