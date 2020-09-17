package com.github.jeuxjeux20.loupsgarous.powers;

import org.jetbrains.annotations.Nullable;

public abstract class LGPower {
    private @Nullable Object source;

    public LGPower(@Nullable Object source) {
        this.source = source;
    }

    public abstract String getName();

    public @Nullable Object getSource() {
        return source;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }
}
