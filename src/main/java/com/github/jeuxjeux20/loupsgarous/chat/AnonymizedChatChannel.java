package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface AnonymizedChatChannel extends LGChatChannel {
    boolean shouldAnonymizeTo(LGPlayer recipient);

    String anonymizeName(LGPlayer player);

    @Override
    default String formatUsername(LGPlayer sender, LGPlayer recipient) {
        if (shouldAnonymizeTo(recipient)) {
            return anonymizeName(sender);
        } else {
            return sender.getName();
        }
    }
}
