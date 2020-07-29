package com.github.jeuxjeux20.loupsgarous.phases.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.descriptor.DescriptorProcessor;
import com.github.jeuxjeux20.loupsgarous.descriptor.DescriptorProcessorAggregator;
import com.github.jeuxjeux20.loupsgarous.descriptor.DescriptorProcessorsModule;
import com.github.jeuxjeux20.loupsgarous.Intrinsic;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public final class LGPhasesDescriptorModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGPhaseDescriptor.Registry.class)
                .to(MinecraftLGPhaseDescriptorRegistry.class);

        bind(LGPhaseDescriptor.Factory.class)
                .annotatedWith(Intrinsic.class)
                .to(IntrinsicLGPhaseDescriptorFactory.class);

        bind(LGPhaseDescriptor.Factory.class)
                .to(EndpointLGPhaseDescriptorFactory.class);

        bind(new TypeLiteral<DescriptorProcessor<LGPhaseDescriptor>>(){})
                .to(new TypeLiteral<DescriptorProcessorAggregator<LGPhaseDescriptor>>(){})
                .in(OrchestratorScoped.class);

        install(new DescriptorProcessorsModule<LGPhaseDescriptor>() {});
    }
}
