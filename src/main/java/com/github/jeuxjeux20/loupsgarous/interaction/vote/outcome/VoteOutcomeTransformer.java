package com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome;

public interface VoteOutcomeTransformer<T> {
    VoteOutcome<T> transform(VoteOutcomeContext<T> context, VoteOutcome<T> outcome);
}
