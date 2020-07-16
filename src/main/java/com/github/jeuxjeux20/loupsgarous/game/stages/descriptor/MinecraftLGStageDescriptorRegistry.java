package com.github.jeuxjeux20.loupsgarous.game.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.BasicDescriptorRegistry;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.google.inject.Inject;

@OrchestratorScoped
public class MinecraftLGStageDescriptorRegistry
        extends BasicDescriptorRegistry<LGStageDescriptor, LGStage>
        implements LGStageDescriptor.Registry {
    @Inject
    MinecraftLGStageDescriptorRegistry(LGStageDescriptor.Factory descriptorFinder) {
        super(descriptorFinder);
    }
}
