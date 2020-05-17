package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.ComponentBased;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface LGGameStage extends ComponentBased {
    /**
     * Determines whether or not this stage should be deleted after it has been ran.
     * <p>
     * This defaults to {@code true}.
     *
     * @return {@code true} if it is temporary, {@code false} if it is not.
     */
    default boolean isTemporary() {
        return false;
    }

    default boolean isLogic() {
        return false;
    }

    default Optional<String> getTitle() {
        return Optional.empty();
    }

    @Nullable String getName();

    default BarColor getBarColor() {
        return BarColor.GREEN;
    }

    LGGameOrchestrator getOrchestrator();

    class Null implements LGGameStage {
        @Override
        public @Nullable String getName() {
            return null;
        }

        @Override
        public LGGameOrchestrator getOrchestrator() {
            return null;
        }
    }
}
