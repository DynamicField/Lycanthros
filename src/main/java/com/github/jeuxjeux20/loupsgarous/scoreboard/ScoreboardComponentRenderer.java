package com.github.jeuxjeux20.loupsgarous.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Singleton;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.github.jeuxjeux20.loupsgarous.scoreboard.ScoreboardComponent.Line;

@Singleton
public final class ScoreboardComponentRenderer {
    public void renderObjective(Objective objective, Collection<ScoreboardComponent> components,
                                LGPlayer player, LGGameOrchestrator orchestrator) {
        List<Line> lines = new ArrayList<>();

        for (ScoreboardComponent component : components) {
            if (component.isSeparated()) {
                component = new SeparatedComponent(component, lines);
            }

            ImmutableList<Line> renderedLines = component.render(player, orchestrator);
            lines.addAll(renderedLines);
        }

        trimEnd(lines);

        int currentScore = Math.max(99, lines.size());
        int whitespaceCount = 1;

        for (Line line : lines) {
            String content = line.getContent();
            @Nullable Integer position = line.getPosition();

            // Because multiple lines can't have the same content,
            // and blank strings are usually meant for new lines,
            // just create a unique string that also represents a new line
            // using whitespaces.
            if (line.isEmpty()) {
                content = Strings.repeat(" ", whitespaceCount);
                whitespaceCount++;
            }

            if (position == null) {
                objective.getScore(content).setScore(currentScore);
                currentScore--;
            } else {
                objective.getScore(content).setScore(position);
                // Don't affect explicitly positioned lines.
            }
        }
    }

    private void trimEnd(List<Line> lines) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            Line line = lines.get(i);

            if (line.isEmpty()) lines.remove(i);
            else break;
        }
    }

    private static class SeparatedComponent implements ScoreboardComponent {
        private final ScoreboardComponent component;
        private final Supplier<@Nullable Line> lastLineSupplier;

        public SeparatedComponent(ScoreboardComponent component, List<Line> lines) {
            this.component = component;
            this.lastLineSupplier = () -> lines.isEmpty() ? null : lines.get(lines.size() - 1);
        }

        @Override
        public ImmutableList<Line> render(LGPlayer player, LGGameOrchestrator orchestrator) {
            ImmutableList.Builder<Line> lines = ImmutableList.builder();

            Line lastLine = lastLineSupplier.get();
            if (lastLine != null && !lastLine.isEmpty()) {
                lines.add(Line.EMPTY);
            }

            ImmutableList<Line> renderedLines = component.render(player, orchestrator);
            lines.addAll(renderedLines);

            if (!renderedLines.isEmpty()) {
                lines.add(Line.EMPTY);
            }

            return lines.build();
        }
    }
}
