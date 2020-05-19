package com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.annotations.Unique;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Map;

public final class UniqueCardCompositionValidator implements CompositionValidator {
    @Override
    public ImmutableSet<Problem> validate(Composition composition) {
        ImmutableSet.Builder<Problem> problemsBuilder = ImmutableSet.builder();

        for (Map.Entry<Class<? extends LGCard>, List<LGCard>> cardGroup : composition.getCardGroups().entrySet()) {
            Class<? extends LGCard> cardClass = cardGroup.getKey();
            LGCard card = cardGroup.getValue().get(0); // The first card will do.
            Unique uniqueAnnotation = cardClass.getAnnotation(Unique.class);

            if (uniqueAnnotation != null) {
                problemsBuilder.addAll(
                        Checks.uniqueCard(composition, cardClass, count -> createProblem(card, uniqueAnnotation, count))
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
