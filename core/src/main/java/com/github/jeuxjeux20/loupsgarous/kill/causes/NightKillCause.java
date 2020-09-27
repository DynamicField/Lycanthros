package com.github.jeuxjeux20.loupsgarous.kill.causes;

import com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.util.WordingUtils;

import java.util.Set;

import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.killMessage;
import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.player;

public final class NightKillCause extends LGKillCause {
    public static final NightKillCause INSTANCE = new NightKillCause();

    private NightKillCause() {}

    @Override
    public String getKillMessage(Set<LGPlayer> players) {
        return killMessage("Le village se lève... sans ") +
               WordingUtils.joiningCommaAnd(players.stream(), this::role) +
               killMessage(".");
    }

    private String role(LGPlayer player) {
        return player(player.getName()) + killMessage(", qui était ") + LGChatStuff.role(player.getCard().getName()) +
               killMessage("");
    }
}
