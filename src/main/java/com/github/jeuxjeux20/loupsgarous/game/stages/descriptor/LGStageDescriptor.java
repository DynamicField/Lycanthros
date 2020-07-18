package com.github.jeuxjeux20.loupsgarous.game.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.descriptor.Descriptor;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.DescriptorFactory;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.DescriptorRegistry;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.StageColor;
import org.jetbrains.annotations.Nullable;

public final class LGStageDescriptor extends Descriptor<LGStage> {
    private @Nullable String name = null;
    private @Nullable String title = null;
    private StageColor color = StageColor.DEFAULT;
    private boolean isTemporary = false;
    private boolean postponesWinConditions = false;

    public LGStageDescriptor(Class<? extends LGStage> describedClass) {
        super(describedClass);
    }

    /**
     * Gets the name of this stage, which is shown on the boss bar.
     * <p>
     * If this method returns {@code null}, no boss bar will be shown.
     *
     * @return the name of this stage, or {@code null} if there isn't
     */
    public @Nullable String getName() {
        return name;
    }

    /**
     * Sets the name of this stage, which is shown on the boss bar.
     * <p>
     * If this method returns {@code null}, no boss bar will be shown.
     *
     * @param name the name of this stage, or {@code null} if there isn't
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * Gets the title of the stage, which is shown in the chat and as a subtitle
     * when the stage starts.
     * <p>
     * If the returned string is {@code null}, no title will be shown.
     *
     * @return the title, or {@code null} if there isn't
     */
    public @Nullable String getTitle() {
        return title;
    }

    /**
     * Sets the title of the stage, which is shown in the chat and as a subtitle
     * when the stage starts.
     * <p>
     * If the returned string is {@code null}, no title will be shown.
     *
     * @param title the title, or {@code null} if there isn't
     */
    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    /**
     * Gets the color of this stage, which is used as the
     * boss bar color and other various places.
     *
     * @return the stage color, with {@code null} represented as {@link StageColor#DEFAULT} instead.
     */
    public StageColor getColor() {
        return color;
    }

    /**
     * Sets the color of this stage, which is used as the
     * boss bar color and other various places.
     *
     * @param color the stage color, a value of {@code null}
     *              sets a {@link StageColor#DEFAULT} value instead
     */
    public void setColor(@Nullable StageColor color) {
        this.color = color;
    }

    /**
     * Determines whether or not this stage should be deleted
     * after it has been processed, in other words, when it has been ran
     * or ignored due to a failing condition.
     * <p>
     * This defaults to {@code false}.
     *
     * @return {@code true} if it is temporary, {@code false} if it is not.
     */
    public boolean isTemporary() {
        return isTemporary;
    }

    /**
     * Determines whether or not this stage should be deleted
     * after it has been processed, in other words, when it has been ran
     * or ignored due to a failing condition.
     * <p>
     * This defaults to {@code false}.
     *
     * @param temporary {@code true} if it is temporary, {@code false} if it is not.
     */
    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

    public boolean postponesWinConditions() {
        return postponesWinConditions;
    }

    public void setPostponesWinConditions(boolean postponesWinConditions) {
        this.postponesWinConditions = postponesWinConditions;
    }

    public interface Registry extends DescriptorRegistry<LGStageDescriptor, LGStage> {
    }

    public interface Factory extends DescriptorFactory<LGStageDescriptor, LGStage> {
    }
}
