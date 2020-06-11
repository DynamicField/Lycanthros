package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.LGMessages;
import com.github.jeuxjeux20.loupsgarous.commands.HandlerCondition;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.google.inject.Inject;
import me.lucko.helper.command.CommandInterruptException;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandHandler;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class InGameHandlerCondition implements HandlerCondition<Player, InGameHandlerCondition.Handler> {
    private final LGGameManager gameManager;

    @Inject
    InGameHandlerCondition(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public FunctionalCommandHandler<Player> wrap(Handler handler) {
        return c -> {
            Optional<LGPlayerAndGame> game = gameManager.getPlayerInGame(c.sender());
            if (!game.isPresent()) {
                c.reply(LGMessages.NOT_IN_GAME);
                return;
            }
            LGPlayer player = game.get().getPlayer();
            LGGameOrchestrator orchestrator = game.get().getOrchestrator();

            handler.handle(c, player, orchestrator);
        };
    }

    @FunctionalInterface
    public interface Handler {
        void handle(CommandContext<Player> context, LGPlayer player, LGGameOrchestrator orchestrator)
                throws CommandInterruptException;
    }
}
