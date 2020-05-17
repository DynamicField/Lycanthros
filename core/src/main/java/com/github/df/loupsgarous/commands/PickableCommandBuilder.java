package com.github.df.loupsgarous.commands;

import com.github.df.loupsgarous.SafeResult;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.Pick;
import com.github.df.loupsgarous.interaction.handler.InteractableCommandHandler;
import com.google.common.reflect.TypeToken;
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
import java.util.Objects;

public final class PickableCommandBuilder<P extends Pick<?>, H extends InteractableCommandHandler<? super P>> {
    private String key;
    private final H handler;
    private final InGameHandlerCondition inGameHandlerCondition;
    private final TypeLiteral<P> pickType;

    private final List<Consumer<FunctionalCommandBuilder<Player>>> commandConfigurators = new ArrayList<>();
    private String failureMessage = "Vous ne pouvez pas faire ça maintenant.";

    @Inject
    public PickableCommandBuilder(H handler,
                                  InGameHandlerCondition inGameHandlerCondition,
                                  TypeLiteral<P> pickType) {
        this.handler = handler;
        this.inGameHandlerCondition = inGameHandlerCondition;
        this.pickType = pickType;
    }

    public PickableCommandBuilder<P, H> failureMessage(String errorMessage) {
        this.failureMessage = Objects.requireNonNull(errorMessage);
        return this;
    }

    public PickableCommandBuilder<P, H> configure(Consumer<FunctionalCommandBuilder<Player>> commandConfigurator) {
        commandConfigurators.add(Objects.requireNonNull(commandConfigurator));
        return this;
    }

    public Command build(String key) {
        this.key = Objects.requireNonNull(key);

        FunctionalCommandBuilder<Player> builder = Commands.create()
                .assertPlayer()
                .assertUsage("<player>", "C'est pas comme ça que ça marche ! {usage}");

        for (Consumer<FunctionalCommandBuilder<Player>> configurator : commandConfigurators) {
            configurator.accept(builder);
        }

        return builder.handler(inGameHandlerCondition.wrap(this::handle));
    }

    @SuppressWarnings("unchecked")
    private void handle(CommandContext<Player> context, LGPlayer player, LGGameOrchestrator orchestrator) {
        SafeResult<P> maybePickable = orchestrator.interactables().single(key)
                .type((TypeToken<P>) TypeToken.of(pickType.getType()))
                .check(p -> p.conditions().checkPicker(player))
                .failureMessage(failureMessage)
                .get();

        maybePickable.ifSuccessOrElse(
                pickable ->  handler.pick(context, player, pickable, orchestrator),
                error -> context.sender().sendMessage(ChatColor.RED + error)
        );
    }
}
