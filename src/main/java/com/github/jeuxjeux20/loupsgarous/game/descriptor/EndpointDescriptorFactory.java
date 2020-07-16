package com.github.jeuxjeux20.loupsgarous.game.descriptor;

import com.google.inject.Inject;

public class EndpointDescriptorFactory<D extends Descriptor<T>, T>
        implements DescriptorFactory<D, T> {
    private final DescriptorFactory<D, T> intrinsicFactory;
    private final DescriptorProcessor<D> processor;

    @Inject
    protected EndpointDescriptorFactory(@Intrinsic DescriptorFactory<D, T> intrinsicFactory, DescriptorProcessor<D> processor) {
        this.intrinsicFactory = intrinsicFactory;
        this.processor = processor;
    }

    @Override
    public D create(Class<? extends T> describedClass) {
        D descriptor = intrinsicFactory.create(describedClass);
        processor.process(descriptor);
        return descriptor;
    }
}
