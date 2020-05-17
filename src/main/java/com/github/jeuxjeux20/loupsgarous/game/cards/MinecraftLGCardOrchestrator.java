package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class MinecraftLGCardOrchestrator implements LGCardOrchestrator {
    private final LGGameOrchestrator orchestrator;

    @Inject
    public MinecraftLGCardOrchestrator(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }

    @Override
    public boolean addTeam(LGCard card, String team) {
        return asMutable(card).getMutableTeams().add(team);
    }

    @Override
    public boolean removeTeam(LGCard card, String team) {
        return asMutable(card).getMutableTeams().remove(team);
    }

    private MutableLGCard asMutable(LGCard card) {
        if (!(card instanceof MutableLGCard)) {
            throw new IllegalArgumentException("The card is not mutable (it does not inherit MutableLGCard).");
        }
        return (MutableLGCard) card;
    }
}
