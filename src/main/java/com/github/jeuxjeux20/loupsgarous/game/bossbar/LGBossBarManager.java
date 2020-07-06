package com.github.jeuxjeux20.loupsgarous.game.bossbar;

import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.module.TerminableModule;

public interface LGBossBarManager extends Terminable {
    void update();

    TerminableModule createUpdateModule();
}
