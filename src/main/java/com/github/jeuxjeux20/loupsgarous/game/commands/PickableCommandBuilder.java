package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.CommandPickHandler;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;
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

public final class PickableCommandBuilder<T extends PickableProvider<P>, P extends Pickable<?>> {
    private final Class<T> pickableClass;
    private final CommandPickHandler<P> handler;
    private final InGameHandlerCondition inGameHandlerCondition;

    private final List<Consumer<FunctionalCommandBuilder<Player>>> commandConfigurators = new ArrayList<>();
    private String cannotPickErrorMessage = "Vous ne pouvez pas faire ça maintenant.";

    @SuppressWarnings("unchecked")
    @Inject
    public PickableCommandBuilder(TypeLiteral<T> pickableType,
                                  CommandPickHandler<P> handler,
                                  InGameHandlerCondition inGameHandlerCondition) {
        this.pickableClass = (Class<T>) pickableType.getRawType(); // It's safe, usually.
        this.handler = handler;
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    public PickableCommandBuilder<T, P> withCannotPickErrorMessage(String errorMessage) {
        this.cannotPickErrorMessage = errorMessage;
        return this;
    }

    public PickableCommandBuilder<T, P> configure(Consumer<FunctionalCommandBuilder<Player>> commandConfigurator) {
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

        Optional<SafeResult<T>> maybePickable
                = stage.getSafeComponent(pickableClass, x -> x.providePickable().canPlayerPick(lgPlayer));

        String errorMessage = maybePickable
                .flatMap(SafeResult::getErrorMessageOptional)
                .orElse(cannotPickErrorMessage);

        P pickable = maybePickable
                .flatMap(SafeResult::getValueOptional)
                .map(PickableProvider::providePickable)
                .orElse(null);

        if (pickable == null) {
            context.sender().sendMessage(ChatColor.RED + errorMessage);
            return;
        }

        handler.pick(context, lgPlayer, pickable, orchestrator);
    }
}
