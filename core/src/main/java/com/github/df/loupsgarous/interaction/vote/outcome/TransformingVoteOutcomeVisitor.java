package com.github.df.loupsgarous.interaction.vote.outcome;

public abstract class TransformingVoteOutcomeVisitor<T> extends VoteOutcomeVisitor<T, VoteOutcome<T>> {
    @Override
    protected VoteOutcome<T> visitOther(VoteOutcome<T> voteOutcome) {
        return voteOutcome;
    }
}
