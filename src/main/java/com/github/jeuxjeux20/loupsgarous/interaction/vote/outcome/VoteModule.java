package com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome;

import com.github.jeuxjeux20.loupsgarous.Intrinsic;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.Vote;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

import static com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeTypes.*;

/**
 * This module enables all the functionality needed for using {@code Vote<T>}:
 * <ul>
 *     <li>
 *     <b>an intrinsic {@code VoteOutcomeDeterminer<T>}</b> which essentially
 *     determines the vote outcome using a relative majority algorithm.
 *     Retrievable using the @{@link Intrinsic} binding annotation.
 *     </li>
 *     <li>
 *     <b>an aggregating {@code VoteOutcomeTransformer<T>}</b> which applies all the
 *     transformers from the {@code Set<VoteOutcomeTransformer<T>>}.
 *     </li>
 *     <li>
 *     <b>an endpoint {@code VoteOutcomeDeterminer<T>}</b> which retrieves the result
 *     of the intrinsic {@code VoteOutcomeDeterminer<T>} and applies the aggregating
 *     {@code VoteOutcomeTransformer<T>} on the result.
 *     </li>
 * </ul>
 *
 * @param <T> the candidate type
 * @see Vote
 * @see VoteOutcomeDeterminer
 * @see VoteOutcomeTransformer
 * @see VoteOutcomeTransformersModule
 */
public class VoteModule<T> extends AbstractModule {
    private final TypeLiteral<T> voteCandidateType;

    public VoteModule(TypeLiteral<T> voteCandidateType) {
        this.voteCandidateType = voteCandidateType;
    }

    public VoteModule(Class<T> voteCandidateType) {
        this(TypeLiteral.get(voteCandidateType));
    }

    @Override
    protected final void configure() {
        bindIntrinsicVoteOutcomeDeterminer();
        bindEndpointVoteOutcomeDeterminer();
        bindVoteOutcomeTransformer();
        bindVoteOutcomeTransformerSet();
    }

    protected void bindIntrinsicVoteOutcomeDeterminer() {
        bind(determiner(voteCandidateType))
                .annotatedWith(Intrinsic.class)
                .to(intrinsicDeterminer(voteCandidateType));
    }

    protected void bindEndpointVoteOutcomeDeterminer() {
        bind(determiner(voteCandidateType))
                .to(endpointDeterminer(voteCandidateType));
    }

    protected void bindVoteOutcomeTransformer() {
        bind(transformer(voteCandidateType))
                .to(transformerAggregator(voteCandidateType));
    }

    protected void bindVoteOutcomeTransformerSet() {
        Multibinder.newSetBinder(binder(), transformer(voteCandidateType));
    }
}
