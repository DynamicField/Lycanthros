package com.github.jeuxjeux20.loupsgarous.game.composition.validation;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.composition.Composition;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Set;
import java.util.stream.Stream;

@Singleton
public final class CompositionValidatorAggregator {
    private final Set<CompositionValidator> compositionValidators;

    @Inject
    CompositionValidatorAggregator(Set<CompositionValidator> compositionValidators) {
        this.compositionValidators = compositionValidators;
    }

    public Stream<CompositionValidator.Problem> validate(Composition composition) {
        return compositionValidators.stream().flatMap(v -> v.validate(composition).stream());
    }
}
