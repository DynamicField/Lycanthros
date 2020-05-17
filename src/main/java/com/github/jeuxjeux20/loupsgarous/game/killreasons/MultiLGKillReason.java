package com.github.jeuxjeux20.loupsgarous.game.killreasons;

import com.github.jeuxjeux20.loupsgarous.game.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import java.util.Collections;
import java.util.List;

public abstract class MultiLGKillReason extends LGKillReason {
    @Override
    public String getKillMessage(LGPlayer player) {
        return getKillMessage(Collections.singletonList(LGKill.of(player, this)));
    }

    public abstract String getKillMessage(List<LGKill> kills);
}
