package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Couple;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Pick;
import com.github.jeuxjeux20.loupsgarous.game.interaction.handler.CoupleInteractableCommandHandler;
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
