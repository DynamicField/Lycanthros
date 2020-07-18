package com.github.jeuxjeux20.loupsgarous.game.interaction.vote.outcome;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class VoteOutcome<T> {
    private final @Nullable T elected;

    public VoteOutcome(@Nullable T elected) {
        this.elected = elected;
    }

    public Optional<T> getElected() {
        return Optional.ofNullable(elected);
    }

    public abstract <V> V accept(VoteOutcomeVisitor<T, V> visitor);
}
