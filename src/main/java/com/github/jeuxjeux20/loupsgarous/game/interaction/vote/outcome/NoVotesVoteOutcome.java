package com.github.jeuxjeux20.loupsgarous.game.interaction.vote.outcome;

public final class NoVotesVoteOutcome<T> extends VoteOutcome<T> {
    public NoVotesVoteOutcome() {
        super(null);
    }

    @Override
    public <V> V accept(VoteOutcomeVisitor<T, V> visitor) {
        return visitor.visit(this);
    }
}
