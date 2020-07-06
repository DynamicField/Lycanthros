package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import me.lucko.helper.text.TextComponent;

public interface AnonymizedChatChannel extends LGChatChannel {
    boolean shouldAnonymizeTo(LGPlayer recipient, LGGameOrchestrator orchestrator);

    String anonymizeName(LGPlayer player, LGGameOrchestrator orchestrator);

    @Override
    default String formatUsername(LGPlayer sender, LGPlayer recipient, LGGameOrchestrator orchestrator) {
        if (shouldAnonymizeTo(recipient, orchestrator)) {
            return anonymizeName(sender, orchestrator);
        } else {
            return sender.getName();
        }
    }
}
