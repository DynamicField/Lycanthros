package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.google.inject.Inject;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.SKULL_SYMBOL;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.killMessage;

public final class RevealAllKillsPhase extends LogicLGPhase {
    @Inject
    RevealAllKillsPhase(LGGameOrchestrator orchestrator) {
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
