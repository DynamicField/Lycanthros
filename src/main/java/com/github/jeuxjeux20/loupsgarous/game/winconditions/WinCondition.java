package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;

import java.util.Optional;

public interface WinCondition {
    Optional<LGEnding> check(LGGame game);
}
