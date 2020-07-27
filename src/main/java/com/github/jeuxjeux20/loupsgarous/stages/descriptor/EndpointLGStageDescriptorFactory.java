package com.github.jeuxjeux20.loupsgarous.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.descriptor.DescriptorProcessor;
import com.github.jeuxjeux20.loupsgarous.descriptor.EndpointDescriptorFactory;
import com.github.jeuxjeux20.loupsgarous.Intrinsic;
import com.github.jeuxjeux20.loupsgarous.stages.LGStage;
import com.google.inject.Inject;

@OrchestratorScoped
final class EndpointLGStageDescriptorFactory
        extends EndpointDescriptorFactory<LGStageDescriptor, LGStage>
        implements LGStageDescriptor.Factory {
    @Inject
    EndpointLGStageDescriptorFactory(@Intrinsic LGStageDescriptor.Factory intrinsicFactory,
                                     DescriptorProcessor<LGStageDescriptor> processor) {
        super(intrinsicFactory, processor);
    }
}
