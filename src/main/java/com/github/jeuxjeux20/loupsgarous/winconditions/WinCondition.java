package com.github.jeuxjeux20.loupsgarous.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;

import java.util.Optional;

public interface WinCondition {
    Optional<LGEnding> check(LGGame game);
}
