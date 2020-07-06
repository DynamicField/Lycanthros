package com.github.jeuxjeux20.loupsgarous.game.actionbar;

import me.lucko.helper.terminable.module.TerminableModule;

public interface LGActionBarManager {
    void update();

    TerminableModule createUpdateModule();
}
