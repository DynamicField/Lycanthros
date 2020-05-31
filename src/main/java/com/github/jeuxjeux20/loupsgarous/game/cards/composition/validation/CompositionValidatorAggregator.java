package com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CompositionValidatorAggregator implements CompositionValidator {
    private final Set<CompositionValidator> compositionValidators;

    @Inject
    CompositionValidatorAggregator(Set<CompositionValidator> compositionValidators) {
        this.compositionValidators = compositionValidators;
    }

    public ImmutableSet<Problem> validate(Composition composition) {
        return compositionValidators.stream()
                .flatMap(v -> v.validate(composition).stream())
                .collect(ImmutableSet.toImmutableSet());
    }
}
