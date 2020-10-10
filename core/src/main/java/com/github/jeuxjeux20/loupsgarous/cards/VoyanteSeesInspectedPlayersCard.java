package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationRequest;
import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationResult;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.powers.VoyantePower;

import java.util.Set;

public class VoyanteSeesInspectedPlayersCard extends CardRevelationModifier {
    @Override
    protected void execute(RevelationRequest<LGCard> request, RevelationResult result) {
        Set<LGPlayer> playersSaw = VoyantePower.PLAYERS_SAW.get(request.getViewer());

        result.setRevealed(playersSaw.contains(request.getHolder()));
    }
}
