package com.github.jeuxjeux20.loupsgarous.cards.composition.util;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.LoupGarouCard;
import com.github.jeuxjeux20.loupsgarous.cards.VillageoisCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultiset;

public final class DefaultCompositions {
    private DefaultCompositions() {
    }

    public static Composition villagerComposition(int playerCount) {
        Preconditions.checkArgument(playerCount > 1, "The player count must be greater than 1.");

        ImmutableMultiset.Builder<LGCard> builder = ImmutableMultiset.builder();

        builder.addCopies(LoupGarouCard.INSTANCE, 1);
        builder.addCopies(VillageoisCard.INSTANCE, playerCount - 1);

        return builder::build;
    }
}
