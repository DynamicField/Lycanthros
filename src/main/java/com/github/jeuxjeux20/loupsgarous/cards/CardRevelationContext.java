package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public class CardRevelationContext {
    private final LGGameOrchestrator orchestrator;
    private final LGPlayer viewer;
    private final LGPlayer holder;
    private LGCard card;

    public CardRevelationContext(LGGameOrchestrator orchestrator,
                                 LGPlayer viewer,
                                 LGPlayer holder,
                                 LGCard card) {
        this.orchestrator = orchestrator;
        this.viewer = viewer;
        this.holder = holder;
        this.card = card;
    }

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    public LGPlayer getViewer() {
        return viewer;
    }

    public LGPlayer getHolder() {
        return holder;
    }

    public LGCard getCard() {
        return card;
    }

    void setCard(LGCard card) {
        this.card = card;
    }
}
