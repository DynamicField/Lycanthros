package com.github.jeuxjeux20.loupsgarous.game.tags;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.inject.Inject;

@OrchestratorScoped
class MinecraftLGTagsOrchestrator implements LGTagsOrchestrator {
    private final MutableLGGameOrchestrator gameOrchestrator;

    @Inject
    MinecraftLGTagsOrchestrator(MutableLGGameOrchestrator gameOrchestrator) {
        this.gameOrchestrator = gameOrchestrator;
    }

    @Override
    public boolean add(LGPlayer player, LGTag tag) {
        return asMutable(player).getMutableTags().add(tag);
    }

    @Override
    public boolean remove(LGPlayer player, LGTag tag) {
        return asMutable(player).getMutableTags().remove(tag);
    }

    private MutableLGPlayer asMutable(LGPlayer player) {
        return gameOrchestrator.game().getPlayer(player)
                .orElseThrow(() -> new IllegalArgumentException(
                        "The given player (" + player + ") is not present in this game orchestrator " +
                        "(" + gameOrchestrator + ")."));
    }
}
