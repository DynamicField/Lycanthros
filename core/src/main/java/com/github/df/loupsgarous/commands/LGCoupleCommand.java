package com.github.df.loupsgarous.commands;

import com.github.df.loupsgarous.interaction.Pick;
import com.github.df.loupsgarous.interaction.Couple;
import com.github.df.loupsgarous.interaction.LGInteractableKeys;
import com.github.df.loupsgarous.interaction.handler.CoupleInteractableCommandHandler;
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
