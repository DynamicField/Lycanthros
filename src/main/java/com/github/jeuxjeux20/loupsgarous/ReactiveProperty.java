package com.github.jeuxjeux20.loupsgarous;

public class ReactiveProperty<T> extends ReactiveValue<T> {
    private T value;

    public ReactiveProperty() {
        this(null);
    }

    public ReactiveProperty(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    protected void setNewValue(T value) {
        this.value = value;
    }
}