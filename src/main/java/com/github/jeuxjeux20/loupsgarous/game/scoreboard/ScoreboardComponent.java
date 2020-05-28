package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

public interface ScoreboardComponent {
    ImmutableList<Line> render(LGPlayer player, LGGameOrchestrator orchestrator);

    default ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of();
    }

    final class Line {
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
    }
}
