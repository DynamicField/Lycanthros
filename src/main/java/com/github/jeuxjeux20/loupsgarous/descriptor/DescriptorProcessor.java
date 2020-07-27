package com.github.jeuxjeux20.loupsgarous.descriptor;

public interface DescriptorProcessor<D extends Descriptor<?>> {
    void process(D descriptor);
}
