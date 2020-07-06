package com.github.jeuxjeux20.loupsgarous.game.kill.reasons;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public final class PlayerQuitKillReason extends SingleLGKillReason {
    public static final PlayerQuitKillReason INSTANCE = new PlayerQuitKillReason();

    private PlayerQuitKillReason() {}

    @Override
    public String getKillMessage(LGPlayer player) {
        return killMessage(player(player.getName())) +
               killMessage(" a quitté la partie, il était ") + role(player.getCard().getName()) + killMessage(".");
    }
}
