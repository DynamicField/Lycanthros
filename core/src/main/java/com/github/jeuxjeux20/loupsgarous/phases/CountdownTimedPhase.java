package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import io.reactivex.rxjava3.core.Observable;

public interface CountdownTimedPhase extends TimedPhase {
    Countdown getCountdown();

    @Override
    default int getSecondsLeft() {
        return getCountdown() == null ? 1 : getCountdown().getTimer();
    }

    @Override
    default int getTotalSeconds() {
        return getCountdown() == null ? 1 : getCountdown().getBiggestTimerValue();
    }

    static Observable<Object> notifyOnTick(Observable<? extends Phase> upstream) {
        return upstream.switchMap(p -> {
            if (p instanceof CountdownTimedPhase) {
                CountdownTimedPhase phase = (CountdownTimedPhase) p;
                return phase.getCountdown().tickUpdates();
            }
            return Observable.just(0);
        });
    }
}
