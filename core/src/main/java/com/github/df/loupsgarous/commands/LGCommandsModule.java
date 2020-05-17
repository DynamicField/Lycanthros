package com.github.df.loupsgarous.commands;

import com.github.df.loupsgarous.CommandsModule;

public final class LGCommandsModule extends CommandsModule {
    @Override
    protected void configureBindings() {
        bind(InGameHandlerCondition.class);
    }

    @Override
    protected void configureCommands() {
        addCommand(LGLookCommand.class);
        addCommand(LGVoteCommand.class);
        addCommand(LGKillCommand.class);
        addCommand(LGPlayersCommand.class);
        addCommand(LGDevoteCommand.class);
        addCommand(LGCompositionCommand.class);
        addCommand(LGKillCommand.class);
        addCommand(LGHealCommand.class);
        addCommand(LGCoupleCommand.class);
        addCommand(LGLobbyCommand.class);
        addCommand(LGQuitCommand.class);
    }
}
