package com.github.df.loupsgarous.interaction;

import com.github.df.loupsgarous.UserFriendlyNamed;
import com.github.df.loupsgarous.game.LGPlayer;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Objects;

public final class Couple implements UserFriendlyNamed {
    private final LGPlayer partner1;
    private final LGPlayer partner2;
    private final ImmutableList<LGPlayer> partners;

    public Couple(LGPlayer partner1, LGPlayer partner2) {
        Preconditions.checkArgument(partner1 != partner2, "Partner 1 cannot be the same as partner 2.");

        this.partner1 = Objects.requireNonNull(partner1);
        this.partner2 = Objects.requireNonNull(partner2);
        partners = ImmutableList.of(partner1, partner2);
    }

    public LGPlayer getPartner1() {
        return partner1;
    }

    public LGPlayer getPartner2() {
        return partner2;
    }

    public ImmutableList<LGPlayer> getPartners() {
        return partners;
    }

    public LGPlayer getOtherPartner(LGPlayer player) {
        if (player == partner1) {
            return partner2;
        } else if (player == partner2) {
            return partner1;
        } else {
            throw new IllegalArgumentException("The given player is not present in the couple.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Couple couple = (Couple) o;
        return Objects.equals(partner1, couple.partner1) &&
               Objects.equals(partner2, couple.partner2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partner1, partner2);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("partner1", partner1)
                .add("partner2", partner2)
                .toString();
    }

    @Override
    public String getUserFriendlyName() {
        return partner1.getName() + " & " + partner2.getName();
    }
}
