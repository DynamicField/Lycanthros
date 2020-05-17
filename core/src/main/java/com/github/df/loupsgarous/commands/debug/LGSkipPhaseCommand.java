package com.github.df.loupsgarous.commands.debug;

import com.github.df.loupsgarous.commands.HelperCommandRegisterer;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.commands.InGameHandlerCondition;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.entity.Player;

public class LGSkipPhaseCommand implements HelperCommandRegisterer {
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGSkipPhaseCommand(InGameHandlerCondition inGameHandlerCondition) {
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .assertPermission("loupsgarous.debug.skipphase")
                .description("Interrupt the current phase's countdown.")
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lg_debug_skipphase");
    }

    private void handle(CommandContext<Player> context, LGPlayer player, LGGameOrchestrator orchestrator) {
        orchestrator.phases().current().stop();
    }
}
