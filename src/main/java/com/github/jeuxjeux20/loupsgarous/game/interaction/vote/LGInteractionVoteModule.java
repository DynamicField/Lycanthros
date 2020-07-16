package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.LGMatchers;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class LGInteractionVoteModule extends VoteOutcomeModifiersModule {
    @Override
    protected void configureBindings() {
        bindInterceptor(Matchers.subclassesOf(Vote.class),
                LGMatchers.predicate(m -> m.getName().equals("getOutcome") && m.getParameterCount() == 0),
                new ApplyVoteModifiersInterceptor(
                        getProvider(Key.get(new TypeLiteral<Map<TypeLiteral<?>, List<VoteOutcomeModifier<?>>>>(){}))
                ));
    }

    @Override
    protected void configureVoteOutcomeModifiers() {
        addVoteOutcomeModifier(MaireVoteOutcomeModifier.class);
    }

    @Provides
    @Singleton
    Map<TypeLiteral<?>, List<VoteOutcomeModifier<?>>> provideVoteOutcomeModifierMap(
            Set<VoteOutcomeModifier<?>> voteOutcomeModifiers) {
        return voteOutcomeModifiers.stream()
                .collect(Collectors.groupingBy(this::findTypeArgument));
    }

    @SuppressWarnings("unchecked")
    private <T> TypeLiteral<T> findTypeArgument(VoteOutcomeModifier<T> m) {
        Type supertype = TypeLiteral.get(m.getClass()).getSupertype(VoteOutcomeModifier.class).getType();
        if (!(supertype instanceof ParameterizedType)) {
            throw new UnsupportedOperationException("Cannot find generic argument for type " + supertype + ": " +
                                                    "it is not a ParameterizedType.");
        }

        Type typeArgument = ((ParameterizedType) supertype).getActualTypeArguments()[0];

        return (TypeLiteral<T>) TypeLiteral.get(typeArgument);
    }
}
