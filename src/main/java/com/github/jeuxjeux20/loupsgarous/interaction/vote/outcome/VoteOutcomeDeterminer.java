package com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome;

public interface VoteOutcomeDeterminer<T> {
    VoteOutcome<T> determine(VoteOutcomeContext<T> context);
}
