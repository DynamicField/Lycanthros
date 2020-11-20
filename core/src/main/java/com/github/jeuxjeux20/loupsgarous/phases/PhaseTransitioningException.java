package com.github.jeuxjeux20.loupsgarous.phases;

public class PhaseTransitioningException extends Exception {
    public PhaseTransitioningException() {
    }

    public PhaseTransitioningException(String message) {
        super(message);
    }

    public PhaseTransitioningException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhaseTransitioningException(Throwable cause) {
        super(cause);
    }
}
