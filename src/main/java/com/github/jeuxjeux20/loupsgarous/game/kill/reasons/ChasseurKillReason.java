package com.github.jeuxjeux20.loupsgarous.game.kill.reasons;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public final class ChasseurKillReason extends LGKillReason {
    @Override
    public String getKillMessage(LGPlayer player) {
        return killMessage("Le chasseur a tiré sa balle sur ") + player(player.getName()) +
               killMessage(", qui était ") + role(player.getCard().getName()) + killMessage(".");
    }
}
