package com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome;

import org.jetbrains.annotations.Nullable;

public final class ForcedVoteOutcome<T> extends VoteOutcome<T> {
    protected ForcedVoteOutcome(@Nullable T elected) {
        super(elected);
    }

    @Override
    public <V> V accept(VoteOutcomeVisitor<T, V> visitor) {
        return visitor.visit(this);
    }
}
