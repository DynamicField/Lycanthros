package com.github.jeuxjeux20.loupsgarous.game.bossbar;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.module.TerminableModule;

public interface LGBossBarManager extends Terminable {
    void update();

    TerminableModule createUpdateModule();

    interface Factory {
        LGBossBarManager create(LGGameOrchestrator orchestrator);
    }
}
