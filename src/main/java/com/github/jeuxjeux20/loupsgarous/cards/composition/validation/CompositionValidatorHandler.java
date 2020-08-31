package com.github.jeuxjeux20.loupsgarous.cards.composition.validation;

import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.extensibility.ExtensionPointHandler;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.COMPOSITION_VALIDATORS;

public final class CompositionValidatorHandler implements CompositionValidator, ExtensionPointHandler {
    private final LGGameOrchestrator orchestrator;

    @Inject
    CompositionValidatorHandler(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public ImmutableSet<Problem> validate(Composition composition) {
        return orchestrator.getGameBundle().contents(COMPOSITION_VALIDATORS).stream()
                .flatMap(v -> v.validate(composition).stream())
                .collect(ImmutableSet.toImmutableSet());
    }
}
