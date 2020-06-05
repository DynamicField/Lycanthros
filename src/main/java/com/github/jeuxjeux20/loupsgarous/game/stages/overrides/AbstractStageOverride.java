package com.github.jeuxjeux20.loupsgarous.game.stages.overrides;

import com.github.jeuxjeux20.loupsgarous.game.stages.RunnableLGGameStage;
import com.google.inject.TypeLiteral;

public abstract class AbstractStageOverride<T extends RunnableLGGameStage> implements StageOverride {
    private final RunnableLGGameStage.Factory<T> stageFactory;
    private final TypeLiteral<T> stageTypeLiteral;

    protected AbstractStageOverride(RunnableLGGameStage.Factory<T> factory, TypeLiteral<T> typeLiteral) {
        this.stageFactory = factory;
        this.stageTypeLiteral = typeLiteral;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends RunnableLGGameStage> getStageClass() {
        // Since we just need the raw type for instanceof, this is safe.
        return (Class<? extends RunnableLGGameStage>) stageTypeLiteral.getRawType();
    }

    @Override
    public RunnableLGGameStage.Factory<?> getStageFactory() {
        return stageFactory;
    }
}
