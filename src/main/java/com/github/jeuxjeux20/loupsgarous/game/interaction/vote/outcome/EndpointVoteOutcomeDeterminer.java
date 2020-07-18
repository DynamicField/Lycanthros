package com.github.jeuxjeux20.loupsgarous.game.interaction.vote.outcome;

import com.github.jeuxjeux20.loupsgarous.game.Intrinsic;
import com.google.inject.Inject;

class EndpointVoteOutcomeDeterminer<T> implements VoteOutcomeDeterminer<T> {
    private final VoteOutcomeDeterminer<T> intrinsicOutcomeDeterminer;
    private final VoteOutcomeTransformer<T> voteOutcomeTransformer;

    @Inject
    EndpointVoteOutcomeDeterminer(@Intrinsic VoteOutcomeDeterminer<T> intrinsicOutcomeDeterminer,
                                  VoteOutcomeTransformer<T> voteOutcomeTransformer) {
        this.intrinsicOutcomeDeterminer = intrinsicOutcomeDeterminer;
        this.voteOutcomeTransformer = voteOutcomeTransformer;
    }

    @Override
    public VoteOutcome<T> determine(VoteOutcomeContext<T> context) {
        VoteOutcome<T> outcome = intrinsicOutcomeDeterminer.determine(context);
        return voteOutcomeTransformer.transform(context, outcome);
    }
}
