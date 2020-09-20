package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.interaction.Couple;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.Pick;
import com.github.jeuxjeux20.loupsgarous.interaction.handler.CoupleInteractableCommandHandler;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGCoupleCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<Pick<Couple>, CoupleInteractableCommandHandler>> commandBuilderProvider;

    @Inject
    LGCoupleCommand(Provider<PickableCommandBuilder<Pick<Couple>, CoupleInteractableCommandHandler>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .build(LGInteractableKeys.COUPLE_CREATOR)
                .register("lgcouple", "lg couple");
    }
}
