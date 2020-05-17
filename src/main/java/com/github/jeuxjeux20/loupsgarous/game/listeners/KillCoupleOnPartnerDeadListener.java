package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.events.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.killreasons.CouplePartnerKillReason;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.stream.Stream;

public class KillCoupleOnPartnerDeadListener implements Listener {
    @EventHandler
    public void onLGKill(LGKillEvent event) {
        for (LGKill kill : event.getKills()) {
            LGPlayer whoDied = kill.getWhoDied();

            Optional<String> coupleTeam = whoDied.getCard().getTeams().stream().filter(LGTeams::isCouple).findFirst();

            coupleTeam.ifPresent(team -> {
                Stream<LGPlayer> partners = event.getGame().getAlivePlayers()
                        .filter(x -> x.getCard().getTeams().contains(team));

                partners.forEach(partner -> killPartner(partner, whoDied, event));
            });
        }
    }

    private void killPartner(LGPlayer partner, LGPlayer me, LGKillEvent event) {
        event.getOrchestrator().killInstantly(partner, new CouplePartnerKillReason(me));
    }
}
