package com.github.jeuxjeux20.loupsgarous.phases.descriptor;

import com.github.jeuxjeux20.loupsgarous.descriptor.DescriptorProcessor;
import com.github.jeuxjeux20.loupsgarous.descriptor.ProcessedDescriptorFactory;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.PhaseInfo;
import com.github.jeuxjeux20.loupsgarous.winconditions.PostponesWinConditions;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.function.Consumer;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.descriptorProcessors;

@OrchestratorScoped
class MinecraftLGPhaseDescriptorFactory
        extends ProcessedDescriptorFactory<LGPhaseDescriptor, LGPhase>
        implements LGPhaseDescriptor.Factory {
    private final LGGameOrchestrator orchestrator;

    @Inject
    MinecraftLGPhaseDescriptorFactory(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public LGPhaseDescriptor createBasic(Class<? extends LGPhase> phaseClass) {
        LGPhaseDescriptor descriptor = new LGPhaseDescriptor(phaseClass);

        applyInfoAnnotation(descriptor);
        applyPostponesWinConditionsAnnotation(descriptor);

        return descriptor;
    }

    @Override
    protected Collection<DescriptorProcessor<LGPhaseDescriptor>> getDescriptorProcessors() {
        return orchestrator.bundle().contents(descriptorProcessors(LGPhaseDescriptor.class));
    }

    private void applyInfoAnnotation(LGPhaseDescriptor descriptor) {
        PhaseInfo annotation = descriptor.getDescribedClass().getAnnotation(PhaseInfo.class);
        if (annotation != null) {
            whenNotEmpty(annotation.name(), descriptor::setName);
            whenNotEmpty(annotation.title(), descriptor::setTitle);
            descriptor.setTemporary(annotation.isTemporary());
            descriptor.setColor(annotation.color());
        }
    }

    private void applyPostponesWinConditionsAnnotation(LGPhaseDescriptor descriptor) {
        if (descriptor.getDescribedClass().isAnnotationPresent(PostponesWinConditions.class)) {
            descriptor.setPostponesWinConditions(true);
        }
    }

    private void whenNotEmpty(String value, Consumer<String> valueSetter) {
        if (StringUtils.isNotEmpty(value)) {
            valueSetter.accept(value);
        }
    }
}
