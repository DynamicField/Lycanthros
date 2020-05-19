package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.CommandsModule;

public final class LGCommandsModule extends CommandsModule {
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
    }
}
