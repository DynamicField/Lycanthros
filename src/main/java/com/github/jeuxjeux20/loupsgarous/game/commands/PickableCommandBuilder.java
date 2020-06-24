package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.handler.CommandPickHandler;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import me.lucko.helper.Commands;
import me.lucko.helper.command.Command;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PickableCommandBuilder<P extends BP, BP extends Pickable<?>> {
    private final Class<P> pickableClass;
    private final CommandPickHandler<BP> handler;
    private final InGameHandlerCondition inGameHandlerCondition;

    private final List<Consumer<FunctionalCommandBuilder<Player>>> commandConfigurators = new ArrayList<>();
    private String cannotPickErrorMessage = "Vous ne pouvez pas faire ça maintenant.";

    @SuppressWarnings("unchecked")
    @Inject
    public PickableCommandBuilder(TypeLiteral<P> pickableType,
                                  CommandPickHandler<BP> handler,
                                  InGameHandlerCondition inGameHandlerCondition) {
        this.pickableClass = (Class<P>) pickableType.getRawType(); // It's safe, usually.
        this.handler = handler;
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    public PickableCommandBuilder<P, BP> withCannotPickErrorMessage(String errorMessage) {
        this.cannotPickErrorMessage = errorMessage;
        return this;
    }

    public PickableCommandBuilder<P, BP> configure(Consumer<FunctionalCommandBuilder<Player>> commandConfigurator) {
        commandConfigurators.add(commandConfigurator);
        return this;
    }

    public Command buildCommand() {
        FunctionalCommandBuilder<Player> builder = Commands.create()
                .assertPlayer()
                .assertUsage("<player>", "C'est pas comme ça que ça marche ! {usage}");

        for (Consumer<FunctionalCommandBuilder<Player>> configurator : commandConfigurators) {
            configurator.accept(builder);
        }

        return builder.handler(inGameHandlerCondition.wrap(this::handle));
    }

    private void handle(CommandContext<Player> context, LGPlayer lgPlayer, LGGameOrchestrator orchestrator) {
        LGStage stage = orchestrator.stages().current();

        Optional<SafeResult<P>> maybePickable
                = stage.getSafeComponent(pickableClass, x -> x.conditions().checkPicker(lgPlayer));

        String errorMessage = maybePickable
                .flatMap(SafeResult::getErrorMessageOptional)
                .orElse(cannotPickErrorMessage);

        P pickable = maybePickable
                .flatMap(SafeResult::getValueOptional)
                .orElse(null);

        if (pickable == null) {
            context.sender().sendMessage(ChatColor.RED + errorMessage);
            return;
        }

        handler.pick(context, lgPlayer, pickable, orchestrator);
    }
}
