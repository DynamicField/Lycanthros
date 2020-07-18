package com.github.jeuxjeux20.loupsgarous.game.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.DescriptorProcessor;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.DescriptorProcessorAggregator;
import com.github.jeuxjeux20.loupsgarous.game.descriptor.DescriptorProcessorsModule;
import com.github.jeuxjeux20.loupsgarous.game.Intrinsic;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public final class LGStagesDescriptorModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGStageDescriptor.Registry.class)
                .to(MinecraftLGStageDescriptorRegistry.class);

        bind(LGStageDescriptor.Factory.class)
                .annotatedWith(Intrinsic.class)
                .to(IntrinsicLGStageDescriptorFactory.class);

        bind(LGStageDescriptor.Factory.class)
                .to(EndpointLGStageDescriptorFactory.class);

        bind(new TypeLiteral<DescriptorProcessor<LGStageDescriptor>>(){})
                .to(new TypeLiteral<DescriptorProcessorAggregator<LGStageDescriptor>>(){})
                .in(OrchestratorScoped.class);

        install(new DescriptorProcessorsModule<LGStageDescriptor>() {});
    }
}
