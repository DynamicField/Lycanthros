package com.github.jeuxjeux20.loupsgarous.mechanic;

public abstract class SpecificMechanicModifier<I extends MechanicRequest, O>
        implements MechanicModifier {
    protected abstract Mechanic<? extends I, ? extends O> getApplicableMechanic();

    @SuppressWarnings("unchecked")
    @Override
    public void execute(MechanicRequest request, Mechanic<?, ?> mechanic, Object result) {
        if (mechanic == getApplicableMechanic()) {
            execute((I) request, (O) result);
        }
    }

    protected abstract void execute(I request, O result);
}
