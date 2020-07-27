package com.github.jeuxjeux20.loupsgarous.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public final class PlayerDeadCardRevealer implements CardRevealer {
    @Override
    public boolean willReveal(LGPlayer viewer, LGPlayer playerToReveal, LGGame game) {
        return playerToReveal.isDead();
    }
}
