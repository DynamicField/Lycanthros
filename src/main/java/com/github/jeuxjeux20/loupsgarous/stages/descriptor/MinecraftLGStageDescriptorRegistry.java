package com.github.jeuxjeux20.loupsgarous.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.descriptor.BasicDescriptorRegistry;
import com.github.jeuxjeux20.loupsgarous.stages.LGStage;
import com.google.inject.Inject;

@OrchestratorScoped
class MinecraftLGStageDescriptorRegistry
        extends BasicDescriptorRegistry<LGStageDescriptor, LGStage>
        implements LGStageDescriptor.Registry {
    @Inject
    MinecraftLGStageDescriptorRegistry(LGStageDescriptor.Factory descriptorFactory) {
        super(descriptorFactory);
    }
}
