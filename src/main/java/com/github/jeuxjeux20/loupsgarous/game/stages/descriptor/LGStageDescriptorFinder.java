package com.github.jeuxjeux20.loupsgarous.game.stages.descriptor;

import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;

public interface LGStageDescriptorFinder {
    LGStageDescriptor find(Class<? extends LGStage> stageClass);
}
