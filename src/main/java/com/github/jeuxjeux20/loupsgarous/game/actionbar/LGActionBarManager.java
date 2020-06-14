package com.github.jeuxjeux20.loupsgarous.game.actionbar;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.bossbar.LGBossBarManager;
import me.lucko.helper.terminable.module.TerminableModule;

public interface LGActionBarManager {
    void update();

    TerminableModule createUpdateModule();

    interface Factory {
        LGActionBarManager create(LGGameOrchestrator orchestrator);
    }
}
