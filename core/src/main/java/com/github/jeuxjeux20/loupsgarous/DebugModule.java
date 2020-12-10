package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.loupsgarous.commands.debug.LGCurrentInteractablesCommand;
import com.github.jeuxjeux20.loupsgarous.commands.debug.LGRegistriesCommand;
import com.github.jeuxjeux20.loupsgarous.commands.debug.LGSkipPhaseCommand;
import com.google.inject.AbstractModule;

public final class DebugModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CommandsModule() {
            @Override
            protected void configureCommands() {
                addCommand(LGSkipPhaseCommand.class);
                addCommand(LGCurrentInteractablesCommand.class);
                addCommand(LGRegistriesCommand.class);
            }
        });
    }
}
