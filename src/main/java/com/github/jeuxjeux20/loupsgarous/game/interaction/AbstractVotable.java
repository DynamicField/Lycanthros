package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import me.lucko.helper.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractVotable<T, E extends Pickable<T>>
        extends AbstractStatefulPickable<T>
        implements Votable<T>, SelfAwareInteractable<E> {

    public AbstractVotable(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public @Nullable T getMajority() {
        // No votes
        if (getPicks().size() == 0) return null;
        // Only one vote
        // ---
        // MdrJeNinja -> LGCramé
        if (getPicks().size() == 1) return getPicks().values().iterator().next();

        Multiset<T> votes = getVotes();

        // Unanimous vote
        // ---
        // ElFamosoLG : 10 votes
        if (votes.elementSet().size() == 1) return votes.iterator().next(); // First item

        List<Multiset.Entry<T>> highestVoteCounts = getTwoHighestVotes(votes);

        Multiset.Entry<T> highestVote = highestVoteCounts.get(0);
        Multiset.Entry<T> secondHighestVote = highestVoteCounts.get(1);

        // If the two highest votes are the same, nobody gets elected.
        // ---
        // ChatonDouteux  : 5 votes    | highestVote
        // SuperMangeChat : 5 votes    | secondHighestVote
        // JeSuisInno     : 4 votes    --------------------
        if (highestVote.getCount() == secondHighestVote.getCount()) return null;

        // If it's not the same count then it's the highestVote gets elected, for good or for worse.
        // ---
        // LGCramé        : 8 votes    | highestVote
        // EncoreUnLG     : 5 votes    | secondHighestVote
        return highestVote.getElement();
    }

    @Override
    public Multiset<T> getVotes() {
        Multiset<T> multiset = HashMultiset.create();

        // Fill the multiset
        getPicks().forEach((from, to) -> multiset.add(to));

        return multiset;
    }

    @Override
    public final void safePick(LGPlayer picker, T target) {
        super.safePick(picker, target);
        Events.call(new LGPickEvent(orchestrator, createPick(picker, target)));
    }

    @Override
    protected final @Nullable T safeRemovePick(LGPlayer picker, boolean isInvalidate) {
        T removedTarget = super.safeRemovePick(picker, isInvalidate);

        if (removedTarget != null) {
            Events.call(new LGPickRemovedEvent(orchestrator, createPick(picker, removedTarget), isInvalidate));
        }

        return removedTarget;
    }

    private Pick<T, E> createPick(LGPlayer picker, T target) {
        return new Pick<>(getEntry(), picker, target);
    }

    @NotNull
    private List<Multiset.Entry<T>> getTwoHighestVotes(Multiset<T> votes) {
        return votes.entrySet().stream()
                .sorted(Comparator.<Multiset.Entry<T>, Integer>comparing(Multiset.Entry::getCount).reversed())
                .limit(2)
                .collect(Collectors.toList());
    }
}
