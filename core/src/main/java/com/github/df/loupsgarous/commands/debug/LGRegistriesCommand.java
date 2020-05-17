package com.github.df.loupsgarous.commands.debug;

import com.github.df.loupsgarous.commands.HelperCommandRegisterer;
import com.github.df.loupsgarous.commands.InGameHandlerCondition;
import com.github.df.loupsgarous.extensibility.registry.GameRegistryKey;
import com.github.df.loupsgarous.extensibility.registry.Registry;
import com.github.df.loupsgarous.extensibility.registry.RegistryEntry;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.relativesorting.OrderConstraints;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LGRegistriesCommand implements HelperCommandRegisterer {
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGRegistriesCommand(InGameHandlerCondition inGameHandlerCondition) {
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .assertPermission("loupsgarous.debug.registries")
                .description("Prints the game registries into the console.")
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lg_debug_registries");
    }

    private void handle(CommandContext<Player> context, LGPlayer player,
                        LGGameOrchestrator orchestrator) {
        System.out.println("Contents of game " + orchestrator + ":");

        for (Map.Entry<GameRegistryKey<?>, Registry<?>> entry :
                orchestrator.getGameRegistries().entrySet()) {
            System.out.println("- " + entry.getKey().getName());

            for (RegistryEntry<?> registryEntry : entry.getValue().getEntries()) {
                System.out.println(formatRegistryEntry(registryEntry));
            }
        }

        context.reply(ChatColor.GREEN + "Please check the console.");
    }

    private String formatRegistryEntry(RegistryEntry<?> entry) {
        StringBuilder stringBuilder = new StringBuilder();

        String identifier = entry.getName();
        if (identifier == null) {
            identifier = "<unnamed>";
        }

        stringBuilder.append(identifier)
                .append(": ")
                .append(entry.getValue())
                .append(" - Ordering: ");

        List<String> orderingAttributes = new ArrayList<>();
        OrderConstraints constraints = entry.getConstraints();

        if (!constraints.getBefore().isEmpty()) {
            String beforeIds = constraints.getBefore().stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));

            orderingAttributes.add("before: [" + beforeIds + "]");
        }

        if (!constraints.getAfter().isEmpty()) {
            String afterIds = constraints.getAfter().stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));

            orderingAttributes.add("after: [" + afterIds + "]");
        }

        if (constraints.getPosition() != 0) {
            orderingAttributes.add("position: " + constraints.getPosition());
        }

        if (orderingAttributes.isEmpty()) {
            stringBuilder.append("<none>");
        } else {
            stringBuilder.append('{')
                    .append(String.join(", ", orderingAttributes))
                    .append('}');
        }

        return stringBuilder.toString();
    }
}
