package com.github.df.loupsgarous.interaction.vote.outcome;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class RelativeMajorityVoteOutcome<T> extends VoteOutcome<T> {
    public RelativeMajorityVoteOutcome(@NotNull T elected) {
        super(Objects.requireNonNull(elected, "elected is null"));
    }

    public T getElectedSure() {
        return getElected().orElseThrow(AssertionError::new);
    }

    @Override
    public <V> V accept(VoteOutcomeVisitor<T, V> visitor) {
        return visitor.visit(this);
    }
}
