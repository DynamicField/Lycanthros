package com.github.jeuxjeux20.loupsgarous.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.cards.composition.util.CompositionFormatUtil;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGCompositionChangeEvent;
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
