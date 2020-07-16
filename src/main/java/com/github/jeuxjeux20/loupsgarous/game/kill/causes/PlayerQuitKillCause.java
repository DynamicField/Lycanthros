package com.github.jeuxjeux20.loupsgarous.game.kill.causes;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public final class PlayerQuitKillCause extends SingleLGKillCause {
    public static final PlayerQuitKillCause INSTANCE = new PlayerQuitKillCause();

    private PlayerQuitKillCause() {}

    @Override
    public String getKillMessage(LGPlayer player) {
        return killMessage(player(player.getName())) +
               killMessage(" a quitté la partie, il était ") + role(player.getCard().getName()) + killMessage(".");
    }
}
