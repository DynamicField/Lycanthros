package com.github.jeuxjeux20.loupsgarous.descriptor;

import com.google.inject.Inject;

import java.util.Set;

public final class DescriptorProcessorAggregator<D extends Descriptor<?>>
        implements DescriptorProcessor<D> {
    private final Set<DescriptorProcessor<D>> descriptorProcessors;

    @Inject
    DescriptorProcessorAggregator(Set<DescriptorProcessor<D>> descriptorProcessors) {
        this.descriptorProcessors = descriptorProcessors;
    }

    @Override
    public void process(D descriptor) {
        for (DescriptorProcessor<D> descriptorProcessor : descriptorProcessors) {
            descriptorProcessor.process(descriptor);
        }
    }
}
