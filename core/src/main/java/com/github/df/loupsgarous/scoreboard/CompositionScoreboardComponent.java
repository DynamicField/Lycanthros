package com.github.df.loupsgarous.scoreboard;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.cards.composition.util.CompositionFormatUtil;
import com.github.df.loupsgarous.event.LGEvent;
import com.github.df.loupsgarous.event.LGKillEvent;
import com.github.df.loupsgarous.event.lobby.LGCompositionChangeEvent;
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
        return ImmutableList.of(LGCompositionChangeEvent.class, LGKillEvent.class);
    }
}
