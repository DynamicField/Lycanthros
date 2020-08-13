package com.github.jeuxjeux20.loupsgarous.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.descriptorProcessors;

@OrchestratorScoped
public final class DescriptorProcessorAggregator<D extends Descriptor<?>>
        implements DescriptorProcessor<D> {
    private final LGGameOrchestrator orchestrator;
    private final Class<D> descriptorType;

    @SuppressWarnings("unchecked")
    @Inject
    DescriptorProcessorAggregator(LGGameOrchestrator orchestrator, TypeLiteral<D> descriptorType) {
        this.orchestrator = orchestrator;
        this.descriptorType = (Class<D>) descriptorType.getRawType();
    }

    @Override
    public void process(D descriptor) {
        for (DescriptorProcessor<D> descriptorProcessor : getDescriptorProcessors()) {
            descriptorProcessor.process(descriptor);
        }
    }

    protected ImmutableSet<DescriptorProcessor<D>> getDescriptorProcessors() {
        return orchestrator.bundle().contents(descriptorProcessors(descriptorType));
    }
}
