package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.RevelationContext;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public class CardRevelationContext extends RevelationContext {
    public CardRevelationContext(LGGameOrchestrator orchestrator,
                                 LGPlayer holder, LGPlayer viewer) {
        super(orchestrator, viewer, holder);
    }

    public LGCard getCard() {
        return getHolder().getCard();
    }
}
