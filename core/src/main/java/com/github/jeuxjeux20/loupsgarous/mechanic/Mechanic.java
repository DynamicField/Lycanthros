package com.github.jeuxjeux20.loupsgarous.mechanic;

import java.util.function.BiConsumer;

public abstract class Mechanic<I extends MechanicRequest, O> {
    public O get(I request) {
        O result = serve(request);

        MechanicModifier.PIPELINE.execute(request, this, result);

        return result;
    }

    protected abstract O serve(I request);

    public final MechanicModifier createModifier(BiConsumer<? super I, ? super O> handler) {
        return new SpecificMechanicModifier<I, O>() {
            @Override
            protected Mechanic<? extends I, ? extends O> getApplicableMechanic() {
                return Mechanic.this;
            }

            @Override
            protected void execute(I request, O result) {
                handler.accept(request, result);
            }
        };
    }
}
