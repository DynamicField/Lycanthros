package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

public abstract class TransformingVoteOutcomeVisitor<T> extends VoteOutcomeVisitor<T, VoteOutcome<T>> {
    @Override
    protected VoteOutcome<T> visitOther(VoteOutcome<T> voteOutcome) {
        return voteOutcome;
    }
}
