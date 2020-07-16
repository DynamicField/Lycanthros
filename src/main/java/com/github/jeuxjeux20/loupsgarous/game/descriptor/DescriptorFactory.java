package com.github.jeuxjeux20.loupsgarous.game.descriptor;

public interface DescriptorFactory<D extends Descriptor<T>, T> {
    D create(Class<? extends T> describedClass);
}
