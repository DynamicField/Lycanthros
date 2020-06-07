package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.ChasseurCard;
import com.github.jeuxjeux20.loupsgarous.game.events.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.stages.ChasseurKillStage;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChasseurKillOnDeathListener implements Listener {
    private final ChasseurKillStage.Factory chasseurStageFactory;

    @Inject
    ChasseurKillOnDeathListener(ChasseurKillStage.Factory chasseurStageFactory) {
        this.chasseurStageFactory = chasseurStageFactory;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLGKill(LGKillEvent event) {
        for (LGKill kill : event.getKills()) {
            LGPlayer whoDied = kill.getWhoDied();

            if (!(whoDied.getCard() instanceof ChasseurCard)) return;

            event.getOrchestrator().stages().add(o -> chasseurStageFactory.create(o, whoDied));
        }
    }
}
