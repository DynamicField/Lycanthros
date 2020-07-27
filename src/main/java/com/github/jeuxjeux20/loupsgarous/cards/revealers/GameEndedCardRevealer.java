package com.github.jeuxjeux20.loupsgarous.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public final class GameEndedCardRevealer implements CardRevealer {
    @Override
    public boolean willReveal(LGPlayer viewer, LGPlayer playerToReveal, LGGame game) {
        return game.getState() == LGGameState.FINISHED;
    }
}
