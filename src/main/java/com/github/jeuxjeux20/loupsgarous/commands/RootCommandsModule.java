package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.CommandsModule;

public final class RootCommandsModule extends CommandsModule {
    @Override
    protected void configureCommands() {
        addCommand(LGStartCommand.class);
        addCommand(LGListCommand.class);
        addCommand(LGFinishCommand.class);
        addCommand(ColorCommand.class);
        addCommand(GuiTestCommand.class);
        addCommand(LGJoinCommand.class);
        addCommand(LGReloadConfigCommand.class);
    }
}
