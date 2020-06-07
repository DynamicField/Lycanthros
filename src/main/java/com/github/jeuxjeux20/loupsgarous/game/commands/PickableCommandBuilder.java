package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.LGMessages;
import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGGameStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PickableProvider;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import me.lucko.helper.Commands;
import me.lucko.helper.command.Command;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandBuilder;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PickableCommandBuilder<T extends PickableProvider> {
    private final LGGameManager gameManager;
    private final Class<T> pickableClass;
    private final LoupsGarous plugin;
    private final InGameHandlerCondition inGameHandlerCondition;

    private final List<Consumer<FunctionalCommandBuilder<Player>>> commandConfigurators = new ArrayList<>();
    private String cannotPickErrorMessage = "Vous ne pouvez pas faire ça maintenant.";

    @SuppressWarnings("unchecked")
    @Inject
    public PickableCommandBuilder(LGGameManager gameManager, TypeLiteral<T> literal, LoupsGarous plugin,
                                  InGameHandlerCondition inGameHandlerCondition) {
        this.gameManager = gameManager;
        this.pickableClass = (Class<T>) literal.getRawType(); // It's safe almost everytime.
        this.plugin = plugin;
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    public PickableCommandBuilder<T> withCannotPickErrorMessage(String errorMessage) {
        this.cannotPickErrorMessage = errorMessage;
        return this;
    }

    public PickableCommandBuilder<T> configure(Consumer<FunctionalCommandBuilder<Player>> commandConfigurator) {
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

        return new CommandTabCompleterDecorator(builder.handler(buildHandler()));
    }

    public FunctionalCommandHandler<Player> buildHandler() {
        return inGameHandlerCondition.wrap(this::handle);
    }

    private void handle(CommandContext<Player> c, LGPlayer lgPlayer, LGGameOrchestrator orchestrator) {
        LGGameStage stage = orchestrator.stages().current();

        String targetName = c.arg(0).value().orElseThrow(AssertionError::new);

        Optional<SafeResult<T>> maybePickable
                = stage.getSafeComponent(pickableClass, x -> x.providePickable().canPlayerPick(lgPlayer));

        String errorMessage = maybePickable
                .flatMap(SafeResult::getErrorMessageOptional)
                .orElse(cannotPickErrorMessage);

        Pickable pickable = maybePickable
                .flatMap(SafeResult::getValueOptional)
                .map(PickableProvider::providePickable)
                .orElse(null);

        if (pickable == null) {
            c.sender().sendMessage(ChatColor.RED + errorMessage);
            return;
        }

        Optional<LGPlayer> maybeTarget = orchestrator.game().findByName(targetName);

        maybeTarget.ifPresent(target -> {
            if (pickable.canPick(lgPlayer, target).sendMessageOnError(c.sender()))
                return;

            pickable.pick(lgPlayer, target);
        });

        if (!maybeTarget.isPresent())
            c.reply(LGMessages.cannotFindPlayer(targetName));
    }

    private class CommandTabCompleterDecorator implements Command {
        private final Command command;

        public CommandTabCompleterDecorator(Command command) {
            this.command = command;
        }

        @Override
        public void register(@Nonnull String... aliases) {
            command.register(aliases);

            CommandTabCompleter completer = new CommandTabCompleter();

            for (String alias : aliases) {
                PluginCommand command = plugin.getCommand(alias);
                if (command != null) {
                    command.setTabCompleter(completer);
                }
            }
        }

        @Override
        public void registerAndBind(@Nonnull TerminableConsumer consumer, @Nonnull String... aliases) {
            command.registerAndBind(consumer, aliases);
        }

        @Override
        public void call(@Nonnull CommandContext<?> context) throws CommandInterruptException {
            command.call(context);
        }

        @Override
        public void close() throws Exception {
            command.close();
        }
    }

    private class CommandTabCompleter implements TabCompleter {
        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command,
                                                    @NotNull String alias, @NotNull String[] args) {
            if (!(sender instanceof Player)) return Collections.emptyList();

            Optional<LGPlayerAndGame> playerInGame = gameManager.getPlayerInGame((Player) sender);
            if (!playerInGame.isPresent()) return Collections.emptyList();

            LGPlayer lgPlayer = playerInGame.get().getPlayer();
            LGGameOrchestrator orchestrator = playerInGame.get().getOrchestrator();

            LGGameStage stage = orchestrator.stages().current();

            Pickable pickable =
                    stage.getComponent(pickableClass, x -> x.providePickable().canPlayerPick(lgPlayer).isSuccess())
                            .map(PickableProvider::providePickable).orElse(null);

            if (pickable != null && args.length == 1) { // Name
                Stream<LGPlayer> players = orchestrator.game().getPlayers().stream();
                return players
                        .filter(target -> pickable.canPick(lgPlayer, target).isSuccess())
                        .map(LGPlayer::getName)
                        .filter(name -> StringUtil.startsWithIgnoreCase(name, args[0]))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }
}
