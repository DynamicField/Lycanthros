package com.github.df.loupsgarous.kill.causes;

import com.github.df.loupsgarous.game.LGPlayer;

import static com.github.df.loupsgarous.chat.LGChatStuff.*;

public final class VillageVoteKillCause extends SingleLGKillCause {
    public static final VillageVoteKillCause INSTANCE = new VillageVoteKillCause();

    private VillageVoteKillCause() {}

    @Override
    public String getKillMessage(LGPlayer player) {
        return killMessage("Le village a décidé de tuer ") + player(player.getName()) +
               killMessage(", qui était ") + role(player.getCard().getName()) + killMessage(".");
    }
}
