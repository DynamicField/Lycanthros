package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.CouplePartnerKillReason;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.stream.Stream;

public class KillCoupleOnPartnerDeadListener implements Listener {
    @EventHandler
    public void onLGKill(LGKillEvent event) {
        for (LGKill kill : event.getKills()) {
            LGPlayer whoDied = kill.getVictim();

            Optional<LGTeam> coupleTeam = whoDied.getCard().getTeams().stream().filter(LGTeams::isCouple).findFirst();

            coupleTeam.ifPresent(team -> {
                Stream<LGPlayer> partners = event.getGame().getAlivePlayers()
                        .filter(x -> x.getCard().getTeams().contains(team));

                partners.forEach(partner -> killPartner(partner, whoDied, event));
            });
        }
    }

    private void killPartner(LGPlayer partner, LGPlayer me, LGKillEvent event) {
        event.getOrchestrator().kills().instantly(partner, new CouplePartnerKillReason(me));
    }
}
