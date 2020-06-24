package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Couple;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.CoupleCreator;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class LGCoupleCommand implements HelperCommandRegisterer {
    private final Provider<PickableCommandBuilder<CoupleCreator, Pickable<Couple>>> commandBuilderProvider;

    @Inject
    LGCoupleCommand(Provider<PickableCommandBuilder<CoupleCreator, Pickable<Couple>>> commandBuilderProvider) {
        this.commandBuilderProvider = commandBuilderProvider;
    }

    @Override
    public void register() {
        commandBuilderProvider.get()
                .buildCommand()
                .register("lgcouple", "lg couple");
    }
}
