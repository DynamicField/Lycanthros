package com.github.df.loupsgarous.game;

import io.reactivex.rxjava3.core.Observable;

public interface StateTransitionBlocker {
    Observable<Boolean> blockingUpdates();

    boolean isBlocking();
}
