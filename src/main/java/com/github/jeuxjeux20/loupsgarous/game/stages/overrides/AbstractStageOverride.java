package com.github.jeuxjeux20.loupsgarous.game.stages.overrides;

import com.github.jeuxjeux20.loupsgarous.game.stages.AsyncLGGameStage;
import com.google.inject.TypeLiteral;

import java.util.List;

public abstract class AbstractStageOverride<T extends AsyncLGGameStage> implements StageOverride {
    private final AsyncLGGameStage.Factory<T> stageFactory;
    private final TypeLiteral<T> stageTypeLiteral;

    protected AbstractStageOverride(AsyncLGGameStage.Factory<T> factory, TypeLiteral<T> typeLiteral) {
        this.stageFactory = factory;
        this.stageTypeLiteral = typeLiteral;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends AsyncLGGameStage> getStageClass() {
        // Since we just need the raw type for instanceof, this is safe.
        return (Class<? extends AsyncLGGameStage>) stageTypeLiteral.getRawType();
    }

    @Override
    public AsyncLGGameStage.Factory<?> getStageFactory() {
        return stageFactory;
    }
}
