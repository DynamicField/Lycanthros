package com.github.df.loupsgarous.cards.composition.validation;

import com.github.df.loupsgarous.cards.LGCard;
import com.github.df.loupsgarous.cards.CupidonCard;
import com.github.df.loupsgarous.cards.composition.Composition;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;

public final class PossibleCouplesCupidonCompositionValidator implements CompositionValidator {
    @Override
    public ImmutableSet<Problem> validate(Composition composition) {
        ImmutableMultiset<LGCard> cards = composition.getContents();

        int cardsCount = cards.size();
        long cupidonCount = cards.count(CupidonCard.INSTANCE);
        int possibleCoupleCount = cardsCount / 2;

        if (cupidonCount > possibleCoupleCount) {
            return ImmutableSet.of(Problem.impossible(
                    "Il est impossible d'avoir " + cupidonCount + " cupidons, car il n'y a pas assez de " +
                    "joueurs pour former tous les couples."
            ));
        } else {
            return ImmutableSet.of();
        }
    }
}
