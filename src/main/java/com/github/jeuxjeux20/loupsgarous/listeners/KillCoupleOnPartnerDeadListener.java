package com.github.jeuxjeux20.loupsgarous.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.kill.causes.CouplePartnerKillCause;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.stream.Stream;

public class KillCoupleOnPartnerDeadListener implements Listener {
    @EventHandler
    public void onLGKill(LGKillEvent event) {
        for (LGKill kill : event.getKills()) {
            LGPlayer whoDied = kill.getVictim();

            Optional<LGTeam> coupleTeam = whoDied.teams().get().stream().filter(LGTeams::isCouple).findFirst();

            coupleTeam.ifPresent(team -> {
                Stream<LGPlayer> partners = event.getGame().getAlivePlayers()
                        .filter(x -> x.teams().get().contains(team));

                partners.forEach(partner -> killPartner(partner, whoDied));
            });
        }
    }

    private void killPartner(LGPlayer partner, LGPlayer me) {
        partner.die(new CouplePartnerKillCause(me));
    }
}
