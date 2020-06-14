package com.github.jeuxjeux20.loupsgarous.game.inventory;

import com.github.jeuxjeux20.loupsgarous.game.HasTriggers;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import me.lucko.helper.event.functional.SubscriptionBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public interface InventoryItem extends HasTriggers {
    boolean isShown(LGPlayer player, LGGameOrchestrator orchestrator);

    ItemStack getItemStack();

    void onClick(LGPlayer player, LGGameOrchestrator orchestrator);

    default @Nullable SubscriptionBuilder<? extends LGEvent> createUpdateSubscription() {
        return null;
    }

    default void onClick(LGPlayerAndGame playerAndGame) {
        onClick(playerAndGame.getPlayer(), playerAndGame.getOrchestrator());
    }
}
