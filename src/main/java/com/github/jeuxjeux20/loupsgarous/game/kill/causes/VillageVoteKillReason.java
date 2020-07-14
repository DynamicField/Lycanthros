package com.github.jeuxjeux20.loupsgarous.game.kill.causes;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public final class VillageVoteKillReason extends SingleLGKillReason {
    public static final VillageVoteKillReason INSTANCE = new VillageVoteKillReason();

    private VillageVoteKillReason() {}

    @Override
    public String getKillMessage(LGPlayer player) {
        return killMessage("Le village a décidé de tuer ") + player(player.getName()) +
               killMessage(", qui était ") + role(player.getCard().getName()) + killMessage(".");
    }
}
