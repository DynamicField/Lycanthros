package com.github.df.loupsgarous.phases;

import io.reactivex.rxjava3.core.Observable;
import org.jetbrains.annotations.Nullable;

public interface PhasesOrchestrator {
    Phase current();

    Observable<Phase> currentUpdates();

    @Nullable PhaseProgram getProgram();

    void startProgram(PhaseProgram program);

    void stopProgram(PhaseProgram program);
}
