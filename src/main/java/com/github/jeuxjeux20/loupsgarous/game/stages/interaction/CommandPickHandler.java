package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandBuilder;
import org.bukkit.entity.Player;

public interface CommandPickHandler<P extends Pickable<?>> {
    void configure(FunctionalCommandBuilder<Player> builder);

    void pick(CommandContext<Player> context, LGPlayer player, P pickable, LGGameOrchestrator orchestrator);
}
