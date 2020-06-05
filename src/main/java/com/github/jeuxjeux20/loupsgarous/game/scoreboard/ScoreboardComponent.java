package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.HasTriggers;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

public interface ScoreboardComponent extends HasTriggers {
    ImmutableList<Line> render(LGPlayer player, LGGameOrchestrator orchestrator);

    default boolean isSeparated() {
        return true;
    }

    final class Line {
        public static final Line EMPTY = new Line();

        private final String content;
        private final @Nullable Integer position;

        public Line() {
            this("");
        }

        public Line(String content) {
            this(content, null);
        }

        public Line(String content, @Nullable Integer position) {
            this.content = content;
            this.position = position;
        }

        public String getContent() {
            return content;
        }

        public @Nullable Integer getPosition() {
            return position;
        }

        public boolean isEmpty() {
            return this == EMPTY || StringUtils.isBlank(content);
        }
    }
}
