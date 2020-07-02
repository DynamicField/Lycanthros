package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

public interface VoteOutcomeModifier<T> {
    VoteOutcome<T> modifyOutcome(VoteOutcomeContext<T> context, VoteOutcome<T> outcome);
}
