package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;

import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.SKULL_SYMBOL;
import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.killMessage;

public final class RevealAllKillsPhase extends LogicPhase {
    public RevealAllKillsPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.getTurn().getTime() == LGGameTurnTime.DAY;
    }

    @Override
    public void start() {
        if (orchestrator.kills().pending().isEmpty()) {
            orchestrator.chat().sendToEveryone(
                    killMessage(SKULL_SYMBOL + " Le village se lève... et personne n'est mort !")
            );
        }
        orchestrator.kills().pending().reveal();
    }
}
