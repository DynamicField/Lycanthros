package com.github.jeuxjeux20.loupsgarous.game.interaction.vote.outcome;

import com.google.inject.TypeLiteral;

import static com.github.jeuxjeux20.loupsgarous.util.TypeUtils.parameterized;
import static com.github.jeuxjeux20.loupsgarous.util.TypeUtils.toLiteralUnchecked;

final class VoteOutcomeTypes {
    private VoteOutcomeTypes() {}

    public static <T> TypeLiteral<VoteOutcomeDeterminer<T>>
    determiner(TypeLiteral<T> type) {
        return toLiteralUnchecked(
                parameterized(VoteOutcomeDeterminer.class, type.getType())
        );
    }

    public static <T> TypeLiteral<IntrinsicVoteOutcomeDeterminer<T>>
    intrinsicDeterminer(TypeLiteral<T> type) {
        return toLiteralUnchecked(
                parameterized(IntrinsicVoteOutcomeDeterminer.class, type.getType())
        );
    }

    public static <T> TypeLiteral<EndpointVoteOutcomeDeterminer<T>>
    endpointDeterminer(TypeLiteral<T> type) {
        return toLiteralUnchecked(
                parameterized(EndpointVoteOutcomeDeterminer.class, type.getType())
        );
    }

    public static <T> TypeLiteral<VoteOutcomeTransformer<T>>
    transformer(TypeLiteral<T> type) {
        return toLiteralUnchecked(
                parameterized(VoteOutcomeTransformer.class, type.getType())
        );
    }

    public static <T> TypeLiteral<VoteOutcomeTransformerAggregator<T>>
    transformerAggregator(TypeLiteral<T> type) {
        return toLiteralUnchecked(
                parameterized(VoteOutcomeTransformerAggregator.class, type.getType())
        );
    }
}
