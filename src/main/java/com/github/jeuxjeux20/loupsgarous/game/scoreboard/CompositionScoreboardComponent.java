package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.util.CompositionFormatUtil;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.lobby.LGLobbyCompositionChangeEvent;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;

public class CompositionScoreboardComponent implements ScoreboardComponent {
    @Override
    public ImmutableList<Line> render(LGPlayer player, LGGameOrchestrator orchestrator) {
        ImmutableList.Builder<Line> lines = ImmutableList.builder();

        lines.add(new Line(ChatColor.AQUA.toString() + ChatColor.BOLD + "Composition"));

        String[] cardNames = CompositionFormatUtil.format(orchestrator.getCurrentComposition()).split("\n");

        for (String cardName : cardNames) {
            lines.add(new Line(cardName));
        }

        return lines.build();
    }

    @Override
    public ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of(LGLobbyCompositionChangeEvent.class, LGKillEvent.class);
    }
}
