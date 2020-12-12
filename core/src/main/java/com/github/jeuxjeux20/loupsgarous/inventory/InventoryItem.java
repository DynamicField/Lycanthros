package com.github.jeuxjeux20.loupsgarous.inventory;

import com.github.jeuxjeux20.loupsgarous.HasTriggers;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryItem implements HasTriggers {
    protected final LGGameOrchestrator orchestrator;

    protected InventoryItem(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public abstract boolean isShown(LGPlayer player);

    public abstract ItemStack render();

    public abstract void onClick(LGPlayer player);
}
