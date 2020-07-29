package com.github.jeuxjeux20.loupsgarous.descriptor;

public interface DescriptorRegistry<D extends Descriptor<T>, T> {
    D get(Class<? extends T> phaseClass);

    void invalidate(Class<? extends T> phaseClass);
}
