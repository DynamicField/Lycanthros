package com.github.df.loupsgarous.commands;

import com.github.df.loupsgarous.CommandsModule;

public final class RootCommandsModule extends CommandsModule {
    @Override
    protected void configureCommands() {
        addCommand(LGStartCommand.class);
        addCommand(LGListCommand.class);
        addCommand(LGFinishCommand.class);
        addCommand(LGJoinCommand.class);
        addCommand(LGReloadConfigCommand.class);
    }
}
