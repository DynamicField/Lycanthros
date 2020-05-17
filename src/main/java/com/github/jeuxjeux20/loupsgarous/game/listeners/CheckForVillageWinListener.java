package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.endings.VillageWonEnding;
import com.github.jeuxjeux20.loupsgarous.game.events.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.winconditions.WinConditionBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CheckForVillageWinListener implements Listener {
    @EventHandler
    public void onKill(LGKillEvent event) {
        WinConditionBuilder
                .create(event.getOrchestrator(), VillageWonEnding::new)
                .onlyAliveTeamPresent(LGTeams.VILLAGEOIS)
                .checkAfterStage();
    }
}
