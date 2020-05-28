package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.ComponentBased;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Stages are a fundamental aspect of the game, they split the game into multiple parts
 * (like in the real tabletop game) and are ran sequentially, in a loop, until
 * something ends the game (like a team winning, for example).
 * <p>
 * The runnable implementation of this interface is {@link AsyncLGGameStage}, or
 * its synchronous counterpart, {@link LogicLGGameStage}.
 * <p>
 * They can be used with a {@link StagesModule}, or by
 * using {@link LGStagesOrchestrator#add(AsyncLGGameStage.Factory)}.
 * <p>
 * The {@link LGStagesModule} contains all the stages of the classic game.
 */
public interface LGGameStage extends ComponentBased {
    /**
     * Determines whether or not this stage should be deleted after it has been ran.
     * <p>
     * This defaults to {@code false}.
     *
     * @return {@code true} if it is temporary, {@code false} if it is not.
     */
    default boolean isTemporary() {
        return false;
    }

    /**
     * Defines if the stage is considered as being used for game logic only,
     * and do not require any user interaction.
     * <p>
     * One example of a game logic would be {@link RevealAllKillsStage}, all it does is
     * just reveal the kills.
     * <p>
     * Note: No boss bar is shown at all for game logic stages.
     *
     * @return whether or not this game is used for game logic only
     */
    default boolean isLogic() {
        return false;
    }

    /**
     * Gets the title of the stage, which is shown in the chat and as a subtitle
     * when the stage starts.
     * <p>
     * If the returned optional is empty, no title and subtitle will be shown.
     *
     * @return an optional containing the title
     */
    default Optional<String> getTitle() {
        return Optional.empty();
    }

    /**
     * Gets the name of this stage, which is shown on the boss bar.
     * <p>
     * If this method returns {@code null}, no boss bar will be shown.
     * Which is the case with {@linkplain #isLogic() game logic} stages.
     *
     * @return the name of this stage, or {@code null} if there isn't
     */
    @Nullable String getName();

    /**
     * Gets the color of the boss bar.
     *
     * @return the boss bar color
     */
    default BarColor getBarColor() {
        return BarColor.GREEN;
    }

    /**
     * Gets the game orchestrator that this stage is linked to.
     *
     * @return the game orchestrator
     */
    LGGameOrchestrator getOrchestrator();

    /**
     * This is the null object for a stage.
     * <p>
     * It returns a null name, and a null orchestrator.
     */
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
