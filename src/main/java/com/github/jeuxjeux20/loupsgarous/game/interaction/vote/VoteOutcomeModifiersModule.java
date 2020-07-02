package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class VoteOutcomeModifiersModule extends AbstractModule {
    private @Nullable Multibinder<VoteOutcomeModifier<?>> voteOutcomeModifierBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureVoteOutcomeModifiers();
    }

    protected void configureBindings() {
    }

    protected void configureVoteOutcomeModifiers() {
    }

    private void actualConfigureVoteOutcomeModifiers() {
        voteOutcomeModifierBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<VoteOutcomeModifier<?>>(){});

        configureVoteOutcomeModifiers();
    }

    protected final void addVoteOutcomeModifier(Class<? extends VoteOutcomeModifier<?>> voteOutcomeModifier) {
        addVoteOutcomeModifier(TypeLiteral.get(voteOutcomeModifier));
    }

    protected final void addVoteOutcomeModifier(TypeLiteral<? extends VoteOutcomeModifier<?>> voteOutcomeModifier) {
        Preconditions.checkState(voteOutcomeModifierBinder != null, "addVoteOutcomeModifier can only be used inside configureVoteOutcomeModifiers()");

        voteOutcomeModifierBinder.addBinding().to(voteOutcomeModifier);
    }
}
