package com.github.jeuxjeux20.loupsgarous.game.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.descriptor.BaseDescriptorProcessorsModule;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.Intrinsic;
import com.google.inject.AbstractModule;

public final class LGStagesDescriptorModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGStageDescriptor.Registry.class).to(MinecraftLGStageDescriptorRegistry.class);

        bind(LGStageDescriptor.Factory.class)
                .annotatedWith(Intrinsic.class)
                .to(IntrinsicLGStageDescriptorFactory.class);

        bind(LGStageDescriptor.Factory.class)
                .to(EndpointLGStageDescriptorFactory.class);

        install(new BaseDescriptorProcessorsModule<LGStageDescriptor>() {});
    }
}
