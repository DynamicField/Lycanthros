package com.github.jeuxjeux20.loupsgarous.listeners;

import com.github.jeuxjeux20.loupsgarous.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.phases.ChasseurKillPhase;
import com.github.jeuxjeux20.loupsgarous.powers.ChasseurPower;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChasseurKillOnDeathListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onLGKill(LGKillEvent event) {
        for (LGKill kill : event.getKills()) {
            LGPlayer victim = kill.getVictim();

            if (victim.powers().has(ChasseurPower.class) && victim.isPresent()) {
                event.getOrchestrator().phases()
                        .insert(new ChasseurKillPhase.Factory(victim));
            }
        }
    }
}
