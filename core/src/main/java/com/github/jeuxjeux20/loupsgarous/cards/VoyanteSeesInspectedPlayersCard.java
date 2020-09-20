package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.powers.VoyantePower;

import java.util.Set;

public final class VoyanteSeesInspectedPlayersCard implements CardRevelationMechanic {
    @Override
    public boolean canHide() {
        return false;
    }

    @Override
    public void execute(CardRevelationContext context) {
        Set<LGPlayer> playersSaw = VoyantePower.PLAYERS_SAW.get(context.getViewer());

        context.setRevealed(playersSaw.contains(context.getHolder()));
    }
}
