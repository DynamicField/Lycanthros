package com.github.jeuxjeux20.loupsgarous.phases.descriptor;

import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.PhaseInfo;
import com.github.jeuxjeux20.loupsgarous.winconditions.PostponesWinConditions;
import org.apache.commons.lang.StringUtils;

import java.util.function.Consumer;

class MinecraftLGPhaseDescriptorFactory implements LGPhaseDescriptor.Factory {
    @Override
    public LGPhaseDescriptor create(Class<? extends LGPhase> describedClass) {
        LGPhaseDescriptor descriptor = new LGPhaseDescriptor(describedClass);

        applyInfoAnnotation(descriptor);
        applyPostponesWinConditionsAnnotation(descriptor);

        return descriptor;
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
