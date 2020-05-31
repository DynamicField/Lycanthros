package com.github.jeuxjeux20.loupsgarous.game.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public final class SelfCardRevealer implements CardRevealer {
    @Override
    public boolean willReveal(LGPlayer playerToReveal, LGPlayer target, LGGameOrchestrator orchestrator) {
        return playerToReveal == target;
    }
}
