package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Couple;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.handler.CoupleCommandPickHandler;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGCoupleCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<Pickable<Couple>, CoupleCommandPickHandler>> commandBuilderProvider;

    @Inject
    LGCoupleCommand(Provider<PickableCommandBuilder<Pickable<Couple>, CoupleCommandPickHandler>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .build(LGInteractableKeys.COUPLE_CREATOR)
                .register("lgcouple", "lg couple");
    }
}
