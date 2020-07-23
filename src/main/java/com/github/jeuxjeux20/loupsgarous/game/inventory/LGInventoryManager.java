package com.github.jeuxjeux20.loupsgarous.game.inventory;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponent;

public interface LGInventoryManager extends OrchestratorComponent {
    void update(LGPlayer player);
}
