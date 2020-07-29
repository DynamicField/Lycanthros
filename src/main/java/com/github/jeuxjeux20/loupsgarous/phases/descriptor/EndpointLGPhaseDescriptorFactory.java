package com.github.jeuxjeux20.loupsgarous.phases.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.descriptor.DescriptorProcessor;
import com.github.jeuxjeux20.loupsgarous.descriptor.EndpointDescriptorFactory;
import com.github.jeuxjeux20.loupsgarous.Intrinsic;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.google.inject.Inject;

@OrchestratorScoped
final class EndpointLGPhaseDescriptorFactory
        extends EndpointDescriptorFactory<LGPhaseDescriptor, LGPhase>
        implements LGPhaseDescriptor.Factory {
    @Inject
    EndpointLGPhaseDescriptorFactory(@Intrinsic LGPhaseDescriptor.Factory intrinsicFactory,
                                     DescriptorProcessor<LGPhaseDescriptor> processor) {
        super(intrinsicFactory, processor);
    }
}
