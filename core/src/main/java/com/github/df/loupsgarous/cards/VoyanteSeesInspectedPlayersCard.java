package com.github.df.loupsgarous.cards;

import com.github.df.loupsgarous.mechanic.RevelationRequest;
import com.github.df.loupsgarous.mechanic.RevelationResult;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.powers.VoyantePower;

import java.util.Set;

public class VoyanteSeesInspectedPlayersCard extends CardRevelationModifier {
    @Override
    protected void execute(RevelationRequest<LGCard> request, RevelationResult result) {
        Set<LGPlayer> playersSaw = request.getViewer().getStored(VoyantePower.PLAYERS_SAW_PROPERTY);

        result.setRevealed(playersSaw.contains(request.getHolder()));
    }
}
