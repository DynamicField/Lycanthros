package com.github.df.loupsgarous.cards.composition.validation;

import com.github.df.loupsgarous.cards.LGCard;
import com.github.df.loupsgarous.cards.composition.Composition;
import com.github.df.loupsgarous.cards.composition.validation.annotations.Unique;
import com.google.common.collect.ImmutableSet;

public final class UniqueCardCompositionValidator implements CompositionValidator {
    @Override
    public ImmutableSet<Problem> validate(Composition composition) {
        ImmutableSet.Builder<Problem> problemsBuilder = ImmutableSet.builder();

        for (LGCard card : composition.getContents().elementSet()) {
            Unique annotation = card.getClass().getAnnotation(Unique.class);

            if (annotation != null) {
                problemsBuilder.addAll(
                        Checks.uniqueCard(composition, card, c -> createProblem(card, annotation, c))
                );
            }
        }

        return problemsBuilder.build();
    }

    private Problem createProblem(LGCard card, Unique uniqueAnnotation, long count) {
        Problem.Type type = uniqueAnnotation.value();
        String message = "Il y a " + count + ' ' + card.getLowercasePluralName() + " mais il doit n'y en avoir " +
                         (card.isFeminineName() ? "qu'une seule" : "qu'un seul") + " !";

        return Problem.of(type, message);
    }
}
