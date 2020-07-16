package com.github.jeuxjeux20.loupsgarous.game.descriptor;

public abstract class AbstractDescriptorFactory<D extends Descriptor<T>, T>
        implements DescriptorFactory<D, T> {
    private final DescriptorProcessor<D> processor;

    protected AbstractDescriptorFactory(DescriptorProcessor<D> processor) {
        this.processor = processor;
    }

    @Override
    public final D create(Class<? extends T> describedClass) {
        D descriptor = findDescriptor(describedClass);
        processor.process(descriptor);
        return descriptor;
    }

    protected D findDescriptor(Class<? extends T> describedClass) {
        return null;
    }
}
