package com.github.jeuxjeux20.loupsgarous.game.killreasons;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public final class PlayerQuitKillReason extends LGKillReason {
    @Override
    public String getKillMessage(LGPlayer player) {
        return killMessage(player(player.getName())) +
                killMessage(" a quitté la partie, il était ") + role(player.getCard().getName()) + killMessage(".");
    }
}
