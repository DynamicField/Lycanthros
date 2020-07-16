package com.github.jeuxjeux20.loupsgarous.game.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.DescriptorProcessor;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.EndpointDescriptorFactory;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.Intrinsic;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.google.inject.Inject;

@OrchestratorScoped
public final class EndpointLGStageDescriptorFactory
        extends EndpointDescriptorFactory<LGStageDescriptor, LGStage>
        implements LGStageDescriptor.Factory {
    @Inject
    EndpointLGStageDescriptorFactory(@Intrinsic LGStageDescriptor.Factory intrinsicFactory,
                                     DescriptorProcessor<LGStageDescriptor> processor) {
        super(intrinsicFactory, processor);
    }
}
