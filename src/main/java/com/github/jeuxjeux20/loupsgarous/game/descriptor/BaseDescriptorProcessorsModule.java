package com.github.jeuxjeux20.loupsgarous.game.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.inject.TypeLiteral;

import static com.github.jeuxjeux20.loupsgarous.util.TypeUtils.parameterized;
import static com.github.jeuxjeux20.loupsgarous.util.TypeUtils.toLiteral;

public abstract class BaseDescriptorProcessorsModule<D extends Descriptor<?>> extends DescriptorProcessorsModule<D> {
    @Override
    protected final void configureBindings() {
        bind(createDescriptorProcessorType()).to(createDescriptorProcessorAggregatorType())
                .in(OrchestratorScoped.class);
    }

    private TypeLiteral<DescriptorProcessorAggregator<D>> createDescriptorProcessorAggregatorType() {
        return toLiteral(
                parameterized(
                        DescriptorProcessorAggregator.class,
                        createDescriptorType()
                )
        );
    }
}
