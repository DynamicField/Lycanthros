package com.github.jeuxjeux20.loupsgarous.game.cards.composition.util;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.LoupGarouCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.VillageoisCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public final class DefaultCompositions {
    private DefaultCompositions() {
    }

    public static Composition villagerComposition(int playerCount) {
        Preconditions.checkArgument(playerCount > 1, "The player count must be greater than 1.");

        ImmutableList.Builder<LGCard> builder = ImmutableList.builder();
        builder.add(new LoupGarouCard());
        for (int i = 0; i < playerCount - 1; i++) {
            builder.add(new VillageoisCard());
        }
        return builder::build;
    }
}
