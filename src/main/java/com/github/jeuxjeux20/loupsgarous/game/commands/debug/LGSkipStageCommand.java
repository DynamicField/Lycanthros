package com.github.jeuxjeux20.loupsgarous.game.commands.debug;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.commands.InGameHandlerCondition;
import com.github.jeuxjeux20.loupsgarous.game.stages.CountdownTimedStage;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.entity.Player;

public class LGSkipStageCommand implements HelperCommandRegisterer {
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGSkipStageCommand(InGameHandlerCondition inGameHandlerCondition) {
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .assertPermission("loupsgarous.debug.skipstage")
                .description("Interrupt the current stage's countdown.")
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lg_debug_skipstage");
    }

    private void handle(CommandContext<Player> context, LGPlayer player, LGGameOrchestrator orchestrator) {
        orchestrator.stages().current().getComponent(CountdownTimedStage.class)
                .map(CountdownTimedStage::getCountdown)
                .ifPresent(Countdown::interrupt);
    }
}
