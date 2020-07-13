package com.github.jeuxjeux20.loupsgarous.game.cards.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface CardRevealer {
    boolean willReveal(LGPlayer viewer, LGPlayer playerToReveal, LGGame game);
}
