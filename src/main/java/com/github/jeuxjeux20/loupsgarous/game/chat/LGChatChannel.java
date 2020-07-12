package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface LGChatChannel extends LGGameOrchestratorDependent {
    String getName();

    boolean isNameDisplayed();

    boolean isReadable(LGPlayer recipient);

    boolean isWritable(LGPlayer sender);

    default String formatUsername(LGPlayer sender, LGPlayer recipient) {
        return sender.getName();
    }
}
