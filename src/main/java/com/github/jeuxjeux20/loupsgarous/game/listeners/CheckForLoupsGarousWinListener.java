package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.endings.LoupsGarousWonEnding;
import com.github.jeuxjeux20.loupsgarous.game.events.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.winconditions.WinConditionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CheckForLoupsGarousWinListener implements Listener {
    @EventHandler
    public void onKill(LGKillEvent event) {
        WinConditionBuilder
                .create(event.getOrchestrator(), LoupsGarousWonEnding::new)
                .onlyAliveTeamPresent(LGTeams.LOUPS_GAROUS)
                .checkAfterStage();
    }
}
