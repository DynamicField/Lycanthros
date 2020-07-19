package com.github.jeuxjeux20.loupsgarous.game.powers;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface LGPowersOrchestrator {
    void add(LGPlayer player, LGPower power);

    boolean remove(LGPlayer player, Class<? extends LGPower> powerClass);
}
