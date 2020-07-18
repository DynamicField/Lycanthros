package com.github.jeuxjeux20.loupsgarous.game.interaction.vote.outcome;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

import static com.github.jeuxjeux20.loupsgarous.util.TypeUtils.genericArgument;
import static com.github.jeuxjeux20.loupsgarous.util.TypeUtils.toLiteralUnchecked;

public abstract class VoteOutcomeTransformersModule<T> extends AbstractModule {
    private @Nullable Multibinder<VoteOutcomeTransformer<T>> voteOutcomeTransformerBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureVoteOutcomeTransformers();
    }

    protected void configureBindings() {
    }

    protected void configureVoteOutcomeTransformers() {
    }

    private void actualConfigureVoteOutcomeTransformers() {
        voteOutcomeTransformerBinder = Multibinder.newSetBinder(binder(), getTransformerType());

        configureVoteOutcomeTransformers();
    }

    private TypeLiteral<VoteOutcomeTransformer<T>> getTransformerType() {
        return VoteOutcomeTypes.transformer(getMyGenericArgument());
    }

    private TypeLiteral<T> getMyGenericArgument() {
        TypeLiteral<?> superclass = TypeLiteral.get(getClass()).getSupertype(VoteOutcomeTransformersModule.class);
        return toLiteralUnchecked(
                genericArgument(superclass, 0)
        );
    }

    protected final void addVoteOutcomeTransformer(Class<? extends VoteOutcomeTransformer<T>> voteOutcomeModifier) {
        addVoteOutcomeTransformer(TypeLiteral.get(voteOutcomeModifier));
    }

    protected final void addVoteOutcomeTransformer(TypeLiteral<? extends VoteOutcomeTransformer<T>> voteOutcomeModifier) {
        Preconditions.checkState(voteOutcomeTransformerBinder != null,
                "addVoteOutcomeTransformer can only be used inside configureVoteOutcomeTransformers()");

        voteOutcomeTransformerBinder.addBinding().to(voteOutcomeModifier);
    }
}
