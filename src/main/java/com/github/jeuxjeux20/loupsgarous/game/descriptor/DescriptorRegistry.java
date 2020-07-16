package com.github.jeuxjeux20.loupsgarous.game.descriptor;

public interface DescriptorRegistry<D extends Descriptor<T>, T> {
    D get(Class<? extends T> stageClass);

    void invalidate(Class<? extends T> stageClass);
}
