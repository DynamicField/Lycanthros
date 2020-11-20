package com.github.jeuxjeux20.loupsgarous.interaction.handler;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorAware;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandBuilder;
import org.bukkit.entity.Player;

public interface InteractableCommandHandler<I extends OrchestratorAware> {
    void configure(FunctionalCommandBuilder<Player> builder);

    void pick(CommandContext<Player> context, LGPlayer player, I interactable, LGGameOrchestrator orchestrator);
}
