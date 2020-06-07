package com.github.jeuxjeux20.loupsgarous.game.kill.reasons;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public final class VillageVoteKillReason extends LGKillReason {
    @Override
    public String getKillMessage(LGPlayer player) {
        return killMessage("Le village a décidé de tuer ") + player(player.getName()) +
               killMessage(", qui était ") + role(player.getCard().getName()) + killMessage(".");
    }
}
