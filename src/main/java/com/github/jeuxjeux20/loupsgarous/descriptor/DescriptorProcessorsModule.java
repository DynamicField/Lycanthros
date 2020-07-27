package com.github.jeuxjeux20.loupsgarous.descriptor;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import static com.github.jeuxjeux20.loupsgarous.util.TypeUtils.*;

public abstract class DescriptorProcessorsModule<D extends Descriptor<?>> extends AbstractModule {
    private @Nullable Multibinder<DescriptorProcessor<D>> descriptorProcessorBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureDescriptorProcessors();
    }

    protected void configureBindings() {
    }

    protected void configureDescriptorProcessors() {
    }

    private void actualConfigureDescriptorProcessors() {
        descriptorProcessorBinder = Multibinder.newSetBinder(binder(), createDescriptorProcessorType());

        configureDescriptorProcessors();
    }

    TypeLiteral<DescriptorProcessor<D>> createDescriptorProcessorType() {
        return toLiteralUnchecked(
                parameterized(
                        DescriptorProcessor.class,
                        createDescriptorType()
                )
        );
    }

    Type createDescriptorType() {
        return genericArgument(toLiteral(getClass()).getSupertype(DescriptorProcessorsModule.class), 0);
    }

    protected final void addDescriptorProcessor(Class<? extends DescriptorProcessor<D>> descriptorProcessor) {
        addDescriptorProcessor(TypeLiteral.get(descriptorProcessor));
    }

    protected final void addDescriptorProcessor(TypeLiteral<? extends DescriptorProcessor<D>> descriptorProcessor) {
        Preconditions.checkState(descriptorProcessorBinder != null,
                "addDescriptorProcessor can only be used inside configureDescriptorProcessors()");

        descriptorProcessorBinder.addBinding().to(descriptorProcessor);
    }
}
