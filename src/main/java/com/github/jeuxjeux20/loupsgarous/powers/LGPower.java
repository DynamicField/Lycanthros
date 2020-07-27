package com.github.jeuxjeux20.loupsgarous.powers;

public abstract class LGPower {
    private final Object source;

    public LGPower(Object source) {
        this.source = source;
    }

    public abstract String getName();

    public Object getSource() {
        return source;
    }
}
