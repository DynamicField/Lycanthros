package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.loupsgarous.commands.debug.ColorCommand;
import com.github.jeuxjeux20.loupsgarous.commands.debug.GuiTestCommand;
import com.github.jeuxjeux20.loupsgarous.game.commands.debug.LGSkipStageCommand;
import com.google.inject.AbstractModule;

public final class DebugModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CommandsModule() {
            @Override
            protected void configureCommands() {
                addCommand(LGSkipStageCommand.class);
                addCommand(ColorCommand.class);
                addCommand(GuiTestCommand.class);
            }
        });
    }
}
