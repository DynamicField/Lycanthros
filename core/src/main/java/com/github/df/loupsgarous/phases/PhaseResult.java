package com.github.df.loupsgarous.phases;

public class PhaseResult {
    private final RunnablePhase phase;
    private final Object source;
    private final PhaseTerminationMethod terminationMethod;

    public PhaseResult(PhaseRunner.RunToken runToken, PhaseTerminationMethod terminationMethod) {
        this(runToken.getPhase(), runToken.getSource(), terminationMethod);
    }

    public PhaseResult(RunnablePhase phase, Object source, PhaseTerminationMethod terminationMethod) {
        this.phase = phase;
        this.source = source;
        this.terminationMethod = terminationMethod;
    }

    public RunnablePhase getPhase() {
        return phase;
    }

    public Object getSource() {
        return source;
    }

    public PhaseTerminationMethod getTerminationMethod() {
        return terminationMethod;
    }
}
