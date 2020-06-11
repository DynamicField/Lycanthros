package com.github.jeuxjeux20.loupsgarous.game.stages.overrides;

import com.github.jeuxjeux20.loupsgarous.game.stages.RunnableLGStage;
import com.google.inject.TypeLiteral;

public abstract class AbstractStageOverride<T extends RunnableLGStage> implements StageOverride {
    private final RunnableLGStage.Factory<T> stageFactory;
    private final TypeLiteral<T> stageTypeLiteral;

    protected AbstractStageOverride(RunnableLGStage.Factory<T> factory, TypeLiteral<T> typeLiteral) {
        this.stageFactory = factory;
        this.stageTypeLiteral = typeLiteral;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends RunnableLGStage> getStageClass() {
        // Since we just need the raw type for instanceof, this is safe.
        return (Class<? extends RunnableLGStage>) stageTypeLiteral.getRawType();
    }

    @Override
    public RunnableLGStage.Factory<?> getStageFactory() {
        return stageFactory;
    }
}
