package com.github.jeuxjeux20.loupsgarous.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.stages.StageInfo;
import com.github.jeuxjeux20.loupsgarous.winconditions.PostponesWinConditions;
import com.google.inject.Singleton;
import org.apache.commons.lang.StringUtils;

import java.util.function.Consumer;

@Singleton
class IntrinsicLGStageDescriptorFactory
        implements LGStageDescriptor.Factory {
    @Override
    public LGStageDescriptor create(Class<? extends LGStage> stageClass) {
        LGStageDescriptor descriptor = new LGStageDescriptor(stageClass);

        applyInfoAnnotation(descriptor);
        applyPostponesWinConditionsAnnotation(descriptor);

        return descriptor;
    }

    private void applyInfoAnnotation(LGStageDescriptor descriptor) {
        StageInfo annotation = descriptor.getDescribedClass().getAnnotation(StageInfo.class);
        if (annotation != null) {
            whenNotEmpty(annotation.name(), descriptor::setName);
            whenNotEmpty(annotation.title(), descriptor::setTitle);
            descriptor.setTemporary(annotation.isTemporary());
            descriptor.setColor(annotation.color());
        }
    }

    private void applyPostponesWinConditionsAnnotation(LGStageDescriptor descriptor) {
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
