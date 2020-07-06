package com.github.jeuxjeux20.loupsgarous.game.teams;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.cards.MutableLGCard;
import com.google.inject.Inject;

@OrchestratorScoped
class MinecraftLGTeamsOrchestrator implements LGTeamsOrchestrator {
    private final MutableLGGameOrchestrator gameOrchestrator;

    @Inject
    public MinecraftLGTeamsOrchestrator(MutableLGGameOrchestrator gameOrchestrator) {
        this.gameOrchestrator = gameOrchestrator;
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return gameOrchestrator;
    }

    @Override
    public boolean add(LGPlayer player, LGTeam team) {
        gameOrchestrator.state().mustBe(LGGameState.STARTED);

        return findCard(player).getMutableTeams().add(team);
    }

    @Override
    public boolean remove(LGPlayer player, LGTeam team) {
        gameOrchestrator.state().mustBe(LGGameState.STARTED);

        return findCard(player).getMutableTeams().remove(team);
    }

    private MutableLGCard findCard(LGPlayer player) {
        return (MutableLGCard) gameOrchestrator.game().getPlayer(player)
                .map(MutableLGPlayer::getCard)
                .orElseThrow(() -> new IllegalArgumentException(
                        "The given player (" + player + ") is not present in this game orchestrator " +
                        "(" + gameOrchestrator + ").")
                );
    }
}
