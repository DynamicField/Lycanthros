package com.github.jeuxjeux20.loupsgarous.interaction;

import me.lucko.helper.terminable.Terminable;

public interface TerminationListener<T extends Terminable> {
    void afterTermination(T terminable);
}
