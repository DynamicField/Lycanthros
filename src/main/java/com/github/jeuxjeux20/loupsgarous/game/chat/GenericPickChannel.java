package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Pickable;

public class GenericPickChannel<T extends Pickable<?>> implements LGChatChannel {
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
    public boolean isReadable(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return pickableProvider.conditions().checkPicker(recipient).isSuccess();
    }

    @Override
    public boolean isWritable(LGPlayer sender, LGGameOrchestrator orchestrator) {
        return false;
    }
}
