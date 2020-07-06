package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import me.lucko.helper.text.TextComponent;

public interface LGChatChannel {
    String getName();

    boolean isNameDisplayed();

    boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator);

    boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator);

    boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator);

    default TextComponent formatUsername(LGPlayer sender, LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return TextComponent.of(sender.getName());
    }
}
