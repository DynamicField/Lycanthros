package com.github.jeuxjeux20.loupsgarous.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.powers.VoyantePower;

public final class VoyanteCardRevealer implements CardRevealer {
    @Override
    public boolean willReveal(LGPlayer viewer, LGPlayer playerToReveal, LGGameOrchestrator orchestrator) {
        /* TODO: Create some sort of "MetadataConfigurator" so we're
                sure we have something in there */
        return viewer.metadata().get(VoyantePower.PLAYERS_SAW_KEY)
                .map(x -> x.contains(playerToReveal)).orElse(false);
    }
}
