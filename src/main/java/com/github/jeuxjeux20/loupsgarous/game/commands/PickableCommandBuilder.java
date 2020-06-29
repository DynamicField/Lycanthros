package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableKey;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.handler.CommandPickHandler;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.inject.Inject;
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

public final class  PickableCommandBuilder<P extends Pickable<?>, H extends CommandPickHandler<? super P>> {
    private InteractableKey<P> key;
    private final H handler;
    private final InGameHandlerCondition inGameHandlerCondition;

    private final List<Consumer<FunctionalCommandBuilder<Player>>> commandConfigurators = new ArrayList<>();
    private String failureMessage = "Vous ne pouvez pas faire ça maintenant.";

    @Inject
    public PickableCommandBuilder(H handler,
                                  InGameHandlerCondition inGameHandlerCondition) {
        this.handler = handler;
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    public PickableCommandBuilder<P, H> failureMessage(String errorMessage) {
        this.failureMessage = Objects.requireNonNull(errorMessage);
        return this;
    }

    public PickableCommandBuilder<P, H> configure(Consumer<FunctionalCommandBuilder<Player>> commandConfigurator) {
        commandConfigurators.add(Objects.requireNonNull(commandConfigurator));
        return this;
    }

    public Command build(InteractableKey<P> key) {
        this.key = Objects.requireNonNull(key);

        FunctionalCommandBuilder<Player> builder = Commands.create()
                .assertPlayer()
                .assertUsage("<player>", "C'est pas comme ça que ça marche ! {usage}");

        for (Consumer<FunctionalCommandBuilder<Player>> configurator : commandConfigurators) {
            configurator.accept(builder);
        }

        return builder.handler(inGameHandlerCondition.wrap(this::handle));
    }

    private void handle(CommandContext<Player> context, LGPlayer player, LGGameOrchestrator orchestrator) {
        SafeResult<P> maybePickable = orchestrator.interactables().single(key)
                .check(p -> p.conditions().checkPicker(player))
                .failureMessage(failureMessage)
                .get();

        maybePickable.ifSuccessOrElse(
                pickable ->  handler.pick(context, player, pickable, orchestrator),
                error -> context.sender().sendMessage(ChatColor.RED + error)
        );
    }
}
