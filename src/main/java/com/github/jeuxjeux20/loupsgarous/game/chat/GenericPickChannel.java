package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;

public class GenericPickChannel<T extends PickableProvider> implements LGChatChannel {
    private final T pickableProvider;

    public GenericPickChannel(T pickableProvider) {
        this.pickableProvider = pickableProvider;
    }

    @Override
    public String getName() {
        return "Pick";
    }

    @Override
    public boolean isNameDisplayed() {
        return false;
    }

    @Override
    public boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator) {
        return false;
    }

    @Override
    public boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return pickableProvider.providePickable().canPlayerPick(recipient).isSuccess();
    }

    @Override
    public boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator) {
        return false;
    }
}
