package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorDependent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import me.lucko.helper.terminable.TerminableConsumer;

import java.util.function.Supplier;

public final class InteractableRegisterer<T extends Interactable> {
    private final Provider<T> interactableProvider;

    @Inject
    private InteractableRegisterer(Provider<T> interactableProvider) {
        this.interactableProvider = interactableProvider;
    }

    public static <T extends Interactable>
    InteractableRegisterer<T> of(Supplier<? extends T> interactableSupplier) {
        return new InteractableRegisterer<>(interactableSupplier::get);
    }

    public TerminableBuilder<T> as(InteractableKey<? super T> key) {
        return new Builder(key);
    }

    public interface TerminableBuilder<T extends Interactable> {
        RegistrableBuilder<T> terminatesWith(TerminableConsumer target);

        default <B extends TerminableConsumer & OrchestratorDependent>
        T boundWith(B target) {
            return terminatesWith(target).in(target.gameOrchestrator());
        }
    }

    public interface RegistrableBuilder<T extends Interactable> {
        T in(LGGameOrchestrator orchestrator);
    }

    private final class Builder implements RegistrableBuilder<T>, TerminableBuilder<T> {
        private TerminableConsumer terminationTrigger;
        private final InteractableKey<? super T> key;

        private Builder(InteractableKey<? super T> key) {
            this.key = key;
        }

        public T in(LGGameOrchestrator orchestrator) {
            if (terminationTrigger == null) {
                throw new IllegalStateException("terminatesWith has not been called.");
            }

            T interactable = interactableProvider.get();
            interactable.bindWith(terminationTrigger);
            orchestrator.interactables().register(key, interactable);

            return interactable;
        }

        @Override
        public RegistrableBuilder<T> terminatesWith(TerminableConsumer target) {
            terminationTrigger = target;
            return this;
        }
    }
}
