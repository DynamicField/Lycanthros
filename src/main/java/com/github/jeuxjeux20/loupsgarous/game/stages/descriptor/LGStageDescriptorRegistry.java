package com.github.jeuxjeux20.loupsgarous.game.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;

public interface LGStageDescriptorRegistry {
    LGStageDescriptor get(Class<? extends LGStage> stageClass);

    void invalidate(Class<? extends LGStage> stageClass);
}
