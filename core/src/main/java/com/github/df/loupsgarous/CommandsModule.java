package com.github.df.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.command.CommandConfigurator;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class CommandsModule extends AbstractModule {
    private @Nullable Multibinder<CommandConfigurator> commandBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureCommands();
    }

    protected void configureBindings() {
    }

    protected void configureCommands() {
    }

    private void actualConfigureCommands() {
        commandBinder = Multibinder.newSetBinder(binder(), CommandConfigurator.class);

        configureCommands();
    }

    protected final void addCommand(Class<? extends CommandConfigurator> command) {
        addCommand(TypeLiteral.get(command));
    }

    protected final void addCommand(TypeLiteral<? extends CommandConfigurator> command) {
        Preconditions.checkState(commandBinder != null, "addCommand can only be used inside configureCommands()");

        commandBinder.addBinding().to(command);
    }
}
