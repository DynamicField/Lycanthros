package com.github.jeuxjeux20.loupsgarous.descriptor;

import java.util.Collection;

public abstract class ProcessedDescriptorFactory<D extends Descriptor<T>, T>
    implements DescriptorFactory<D, T> {
    @Override
    public final D create(Class<? extends T> describedClass) {
        D descriptor = createBaseDescriptor(describedClass);
        for (DescriptorProcessor<D> descriptorProcessor : getDescriptorProcessors()) {
            descriptorProcessor.process(descriptor);
        }
        return descriptor;
    }

    protected abstract D createBaseDescriptor(Class<? extends T> describedClass);

    protected abstract Collection<DescriptorProcessor<D>> getDescriptorProcessors();
}
