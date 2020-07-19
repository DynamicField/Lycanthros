package com.github.jeuxjeux20.loupsgarous.game.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.powers.VoyantePower;

public final class VoyanteCardRevealer implements CardRevealer {
    @Override
    public boolean willReveal(LGPlayer viewer, LGPlayer playerToReveal, LGGame game) {
        /* TODO: Create some sort of "MetadataConfigurator" so we're
                sure we have something in there */
        return viewer.metadata().get(VoyantePower.PLAYERS_SAW_KEY)
                .map(x -> x.contains(playerToReveal)).orElse(false);
    }
}
