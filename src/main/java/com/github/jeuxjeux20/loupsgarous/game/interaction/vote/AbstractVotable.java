package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.*;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import me.lucko.helper.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractVotable<T>
        extends AbstractStatefulPickable<T>
        implements Votable<T>, SelfAwareInteractable {
    private final Dependencies<T> dependencies;

    public AbstractVotable(LGGameOrchestrator orchestrator, Dependencies<T> dependencies) {
        super(orchestrator);
        this.dependencies = dependencies;
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoteOutcome<T> getOutcome() {
        // Safe because this is an abstract class and they *always* have generic information
        // about their generic argument (superclass).
        VoteOutcomeContext<T> context = new VoteOutcomeContext<>(getVotes(), getPicks(),
                (Class<? extends Votable<T>>) getClass(), orchestrator);

        VoteOutcome<T> outcome = getActualOutcome(context);
        for (VoteOutcomeModifier<T> voteOutcomeModifier : dependencies.voteOutcomeModifiers) {
            outcome = voteOutcomeModifier.modifyOutcome(context, outcome);
        }
        return outcome;
    }

    private VoteOutcome<T> getActualOutcome(VoteOutcomeContext<T> context) {
        if (context.getPicks().size() == 0) {
            return new NoVotesVoteOutcome<>();
        }

        List<Multiset.Entry<T>> sameVotesCandidates = getHighestSameVotesCandidates(context.getVotes());

        if (sameVotesCandidates.size() == 1) {
            return new RelativeMajorityVoteOutcome<>(sameVotesCandidates.get(0).getElement());
        } else {
            List<T> conflictingCandidates = sameVotesCandidates.stream()
                    .map(Multiset.Entry::getElement)
                    .collect(Collectors.toList());

            return new IndecisiveVoteOutcome<>(conflictingCandidates);
        }
    }

    @Override
    public Multiset<T> getVotes() {
        return HashMultiset.create(getPicks().values());
    }

    @Override
    protected final void safePick(LGPlayer picker, T target) {
        super.safePick(picker, target);

        if (canCallEvent()) {
            Events.call(new LGPickEvent(orchestrator, createPick(picker, target)));
        }
    }

    @Override
    protected final @Nullable T safeRemovePick(LGPlayer picker, boolean isInvalidate) {
        T removedTarget = super.safeRemovePick(picker, isInvalidate);

        if (removedTarget != null && canCallEvent()) {
            Events.call(new LGPickRemovedEvent(orchestrator, createPick(picker, removedTarget), isInvalidate));
        }

        return removedTarget;
    }

    private Pick<T, ?> createPick(LGPlayer picker, T target) {
        return new Pick<>(getEntry(), picker, target);
    }

    private boolean canCallEvent() {
        return orchestrator.interactables().has(getEntry());
    }

    @Override
    public abstract InteractableEntry<? extends Pickable<T>> getEntry();

    @NotNull
    private List<Multiset.Entry<T>> getHighestSameVotesCandidates(Multiset<T> votes) {
        List<Multiset.Entry<T>> votesDescending = votes.entrySet().stream()
                .sorted(Comparator.<Multiset.Entry<T>, Integer>comparing(Multiset.Entry::getCount).reversed())
                .collect(Collectors.toList());

        List<Multiset.Entry<T>> results = new ArrayList<>();
        int lastCount = -1;

        for (Multiset.Entry<T> entry : votesDescending) {
            int count = entry.getCount();
            if (lastCount != -1 && lastCount != count) {
                break;
            }

            results.add(entry);
            lastCount = count;
        }

        return results;
    }

    public static class Dependencies<T> {
        public final List<VoteOutcomeModifier<T>> voteOutcomeModifiers;

        @SuppressWarnings("unchecked")
        @Inject
        Dependencies(Map<TypeLiteral<?>, List<VoteOutcomeModifier<?>>> voteOutcomeModifierMap,
                     TypeLiteral<T> candidateType) {
            this.voteOutcomeModifiers = (List<VoteOutcomeModifier<T>>) (List<?>)
                    voteOutcomeModifierMap.getOrDefault(candidateType, Collections.emptyList());
        }
    }
}
