package com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.google.common.collect.ImmutableSet;

public class MultipleTeamsCompositionValidator implements CompositionValidator {
    @Override
    public ImmutableSet<Problem> validate(Composition composition) {
        if (getDistinctTeamsCount(composition) == 1) {
            return ImmutableSet.of(Problem.impossible("La composition ne contient qu'un seul camp."));
        }
        return ImmutableSet.of();
    }

    private long getDistinctTeamsCount(Composition composition) {
        return composition.getCards().stream().flatMap(x -> x.getTeams().stream()).distinct().count();
    }
}
