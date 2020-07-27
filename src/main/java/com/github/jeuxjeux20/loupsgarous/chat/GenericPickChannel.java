package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.Pick;

public class GenericPickChannel<T extends Pick<?>> extends AbstractLGChatChannel {
    private final T pickableProvider;

    public GenericPickChannel(T pickableProvider, LGGameOrchestrator orchestrator) {
        super(orchestrator);
        this.pickableProvider = pickableProvider;
    }

    @Override
    public String getName() {
        return "PickData";
    }

    @Override
    public boolean isNameDisplayed() {
        return false;
    }

    @Override
    public boolean isReadable(LGPlayer recipient) {
        return pickableProvider.conditions().checkPicker(recipient).isSuccess();
    }

    @Override
    public boolean isWritable(LGPlayer sender) {
        return false;
    }
}
