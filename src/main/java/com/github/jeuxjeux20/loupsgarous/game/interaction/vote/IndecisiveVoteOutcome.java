package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public final class IndecisiveVoteOutcome<T> extends VoteOutcome<T> {
    private final ImmutableSet<T> conflictingCandidates;

    public IndecisiveVoteOutcome(Collection<T> conflictingCandidates) {
        super(null);
        this.conflictingCandidates = ImmutableSet.copyOf(conflictingCandidates);
    }

    public ImmutableSet<T> getConflictingCandidates() {
        return conflictingCandidates;
    }

    @Override
    public <V> V accept(VoteOutcomeVisitor<T, V> visitor) {
        return visitor.visit(this);
    }
}
