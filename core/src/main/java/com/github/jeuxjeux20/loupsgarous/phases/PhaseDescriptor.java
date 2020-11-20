package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.winconditions.PostponesWinConditions;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

public final class PhaseDescriptor {
    private @Nullable String name = null;
    private @Nullable String title = null;
    private PhaseColor color = PhaseColor.DEFAULT;
    private boolean isTemporary = false;
    private boolean postponesWinConditions = false;

    public static PhaseDescriptor fromClass(Class<? extends Phase> clazz) {
        PhaseDescriptor descriptor = new PhaseDescriptor();

        PhaseInfo annotation = clazz.getAnnotation(PhaseInfo.class);
        if (annotation != null) {
            if (StringUtils.isNotBlank(annotation.name())) {
                descriptor.setName(annotation.name());
            }
            if (StringUtils.isNotBlank(annotation.title())) {
                descriptor.setTitle(annotation.title());
            }
            descriptor.setTemporary(annotation.isTemporary());
            descriptor.setColor(annotation.color());
        }


        if (clazz.isAnnotationPresent(PostponesWinConditions.class)) {
            descriptor.setPostponesWinConditions(true);
        }

        return descriptor;
    }

    /**
     * Gets the name of this phase, which is shown on the boss bar.
     * <p>
     * If this method returns {@code null}, no boss bar will be shown.
     *
     * @return the name of this phase, or {@code null} if there isn't
     */
    public @Nullable String getName() {
        return name;
    }

    /**
     * Sets the name of this phase, which is shown on the boss bar.
     * <p>
     * If this method returns {@code null}, no boss bar will be shown.
     *
     * @param name the name of this phase, or {@code null} if there isn't
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * Gets the title of the phase, which is shown in the chat and as a subtitle when the phase
     * starts.
     * <p>
     * If the returned string is {@code null}, no title will be shown.
     *
     * @return the title, or {@code null} if there isn't
     */
    public @Nullable String getTitle() {
        return title;
    }

    /**
     * Sets the title of the phase, which is shown in the chat and as a subtitle when the phase
     * starts.
     * <p>
     * If the returned string is {@code null}, no title will be shown.
     *
     * @param title the title, or {@code null} if there isn't
     */
    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    /**
     * Gets the color of this phase, which is used as the boss bar color and other various places.
     *
     * @return the phase color, with {@code null} represented as {@link PhaseColor#DEFAULT} instead.
     */
    public PhaseColor getColor() {
        return color;
    }

    /**
     * Sets the color of this phase, which is used as the boss bar color and other various places.
     *
     * @param color the phase color, a value of {@code null} sets a {@link PhaseColor#DEFAULT} value
     *              instead
     */
    public void setColor(@Nullable PhaseColor color) {
        this.color = color;
    }

    /**
     * Determines whether or not this phase should be deleted after it has been processed, in other
     * words, when it has been ran or ignored due to a failing condition.
     * <p>
     * This defaults to {@code false}.
     *
     * @return {@code true} if it is temporary, {@code false} if it is not.
     */
    public boolean isTemporary() {
        return isTemporary;
    }

    /**
     * Determines whether or not this phase should be deleted after it has been processed, in other
     * words, when it has been ran or ignored due to a failing condition.
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
}
