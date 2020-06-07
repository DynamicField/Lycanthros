package com.github.jeuxjeux20.loupsgarous.game.kill.reasons;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.bukkit.ChatColor;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public final class CouplePartnerKillReason extends LGKillReason {
    private final LGPlayer partner;

    public CouplePartnerKillReason(LGPlayer partner) {
        this.partner = partner;
    }

    @Override
    public String getKillMessage(LGPlayer player) {
        return killMessage("L'amour envers ") + ChatColor.ITALIC + partner.getName() + killMessage(" est tellement fort que ")
               + player(player.getName()) + killMessage(", ") + role(player.getCard().getName())
               + killMessage(" s'est suicidé.");
    }

    public LGPlayer getPartner() {
        return partner;
    }
}
