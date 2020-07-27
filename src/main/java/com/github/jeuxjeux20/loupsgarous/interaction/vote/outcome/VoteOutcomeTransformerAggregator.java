package com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome;

import com.google.inject.Inject;

import java.util.Set;

class VoteOutcomeTransformerAggregator<T> implements VoteOutcomeTransformer<T> {
    private final Set<VoteOutcomeTransformer<T>> voteOutcomeTransformers;

    @Inject
    VoteOutcomeTransformerAggregator(Set<VoteOutcomeTransformer<T>> voteOutcomeTransformers) {
        this.voteOutcomeTransformers = voteOutcomeTransformers;
    }

    @Override
    public VoteOutcome<T> transform(VoteOutcomeContext<T> context, VoteOutcome<T> outcome) {
        for (VoteOutcomeTransformer<T> transformer : voteOutcomeTransformers) {
            outcome = transformer.transform(context, outcome);
        }

        return outcome;
    }
}
