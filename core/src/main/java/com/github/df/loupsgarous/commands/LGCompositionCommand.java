package com.github.df.loupsgarous.commands;

import com.github.df.loupsgarous.chat.LGMessages;
import com.github.df.loupsgarous.game.LGGameManager;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayerAndGame;
import com.github.df.loupsgarous.cards.composition.util.CompositionFormatUtil;
import com.google.inject.Inject;
import me.lucko.helper.Commands;

import java.util.Optional;

import static com.github.df.loupsgarous.chat.LGChatStuff.banner;

public class LGCompositionCommand implements HelperCommandRegisterer {
    private final LGGameManager gameManager;

    @Inject
    LGCompositionCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .handler(c -> {
                    Optional<LGPlayerAndGame> game = gameManager.getPlayerInGame(c.sender());
                    if (!game.isPresent()) {
                        c.reply(LGMessages.NOT_IN_GAME);
                        return;
                    }
                    LGGameOrchestrator orchestrator = game.get().getOrchestrator();

                    String message = banner("Composition") + '\n' +
                                     CompositionFormatUtil.format(orchestrator.getCurrentComposition());

                    c.reply(message);
                })
                .register("lgcomposition", "lg composition");
    }
}
