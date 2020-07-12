package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class ApplyVoteModifiersInterceptor implements MethodInterceptor {
    private final Provider<Map<TypeLiteral<?>, List<VoteOutcomeModifier<?>>>> outcomeModifierMapProvider;

    public ApplyVoteModifiersInterceptor(
            Provider<Map<TypeLiteral<?>, List<VoteOutcomeModifier<?>>>> outcomeModifierMapProvider) {
        this.outcomeModifierMapProvider = outcomeModifierMapProvider;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Votable<?> self = (Votable<?>) invocation.getThis();
        return run(self, invocation);
    }

    @SuppressWarnings("unchecked")
    private <T> VoteOutcome<T> run(Votable<T> self, MethodInvocation invocation) throws Throwable {
        List<VoteOutcomeModifier<T>> outcomeModifiers = getOutcomeModifiers(self);

        VoteOutcomeContext<T> context = createContext(self);

        VoteOutcome<T> outcome = (VoteOutcome<T>) invocation.proceed();
        if (outcomeModifiers != null) {
            for (VoteOutcomeModifier<T> voteOutcomeModifier : outcomeModifiers) {
                outcome = voteOutcomeModifier.modifyOutcome(context, outcome);
            }
        }
        return outcome;
    }

    private <T> VoteOutcomeContext<T> createContext(Votable<T> self) {
        return new VoteOutcomeContext<>(self.getVotes(), self.getPicks(), self, self.gameOrchestrator());
    }

    @SuppressWarnings("unchecked")
    private <T> @Nullable List<VoteOutcomeModifier<T>> getOutcomeModifiers(Votable<T> votable) {
        TypeLiteral<?> parameterizedVotable = TypeLiteral.get(votable.getClass()).getSupertype(Votable.class);
        Type argument = ((ParameterizedType) parameterizedVotable.getType()).getActualTypeArguments()[0];
        TypeLiteral<?> argumentLiteral = TypeLiteral.get(argument);

        // Safe because of the provider.
        return (List<VoteOutcomeModifier<T>>) (List<?>)
                outcomeModifierMapProvider.get().get(argumentLiteral);
    }
}
