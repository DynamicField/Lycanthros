package com.github.jeuxjeux20.loupsgarous.game.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.VoyanteCard;

public final class VoyanteCardRevealer implements CardRevealer {
    @Override
    public boolean willReveal(LGPlayer viewer, LGPlayer playerToReveal, LGGameOrchestrator orchestrator) {
        if (viewer.getCard() instanceof VoyanteCard) {
            VoyanteCard card = (VoyanteCard) viewer.getCard();

            return card.getPlayersSaw().contains(playerToReveal);
        }
        return false;
    }
}
