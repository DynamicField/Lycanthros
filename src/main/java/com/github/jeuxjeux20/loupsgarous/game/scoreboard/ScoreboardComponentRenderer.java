package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.base.Strings;
import com.google.inject.Singleton;
import org.apache.commons.lang.StringUtils;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class ScoreboardComponentRenderer {
    public void renderObjective(Objective objective, Collection<ScoreboardComponent> components,
                                LGPlayer player, LGGameOrchestrator orchestrator) {
        List<ScoreboardComponent.Line> lines = components.stream()
                .flatMap(c -> c.render(player, orchestrator).stream())
                .collect(Collectors.toList());

        int currentScore = Math.max(99, lines.size());
        int whitespaceCount = 1;

        for (ScoreboardComponent.Line line : lines) {
            String content = line.getContent();
            @Nullable Integer position = line.getPosition();

            // Because multiple lines can't have the same content,
            // and blank strings are usually meant for new lines,
            // just create a unique string that also represents a new line
            // using whitespaces.
            if (StringUtils.isBlank(content)) {
                content = Strings.repeat(" ", whitespaceCount);
                whitespaceCount++;
            }

            if (position == null) {
                objective.getScore(content).setScore(currentScore);
                currentScore--;
            } else {
                objective.getScore(content).setScore(position);
                // Don't affect automatically positioned lines.
            }
        }
    }
}
