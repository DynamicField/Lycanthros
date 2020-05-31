package com.github.jeuxjeux20.loupsgarous.game.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.VoyanteCard;

public final class VoyanteCardRevealer implements CardRevealer {
    @Override
    public boolean willReveal(LGPlayer playerToReveal, LGPlayer target, LGGameOrchestrator orchestrator) {
        if (target.getCard() instanceof VoyanteCard) {
            VoyanteCard card = (VoyanteCard) target.getCard();

            return card.getPlayersSaw().contains(playerToReveal);
        }
        return false;
    }
}
