package com.github.df.loupsgarous.interaction.vote.outcome;

public abstract class VoteOutcomeVisitor<T, V> {
    public V visit(ForcedVoteOutcome<T> forcedVoteOutcome) {
        return visitOther(forcedVoteOutcome);
    }

    public V visit(IndecisiveVoteOutcome<T> indecisiveVoteOutcome) {
        return visitOther(indecisiveVoteOutcome);
    }

    public V visit(RelativeMajorityVoteOutcome<T> relativeMajorityVoteOutcome) {
        return visitOther(relativeMajorityVoteOutcome);
    }

    public V visit(NoVotesVoteOutcome<T> noVotesVoteOutcome) {
        return visitOther(noVotesVoteOutcome);
    }

    public V visitExtension(VoteOutcome<T> voteOutcome) {
        return visitOther(voteOutcome);
    }

    protected V visitOther(VoteOutcome<T> voteOutcome) {
        return null;
    }
}
