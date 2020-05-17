package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.endings.EveryoneDeadEnding;
import com.github.jeuxjeux20.loupsgarous.game.events.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.winconditions.WinConditionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CheckForEveryoneDeadListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLGKill(LGKillEvent event) {
        WinConditionBuilder
                .create(event.getOrchestrator(), EveryoneDeadEnding::new)
                .everyoneDead()
                .checkAfterStage();
    }
}
