package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import me.lucko.helper.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractVotable<T, E extends Pickable<T>>
        extends AbstractStatefulPickable<T>
        implements Votable<T>, SelfAwareInteractable<E> {

    public AbstractVotable(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public @Nullable T getMajorityTarget() {
        // No votes
        if (getPicks().size() == 0) return null;
        // Only one vote
        // ---
        // MdrJeNinja -> LGCramé
        if (getPicks().size() == 1) return getPicks().values().iterator().next();

        Map<T, Integer> votedTargetsCount = getTargetVoteCount();

        // Unanimous vote
        // ---
        // ElFamosoLG : 10 votes
        if (votedTargetsCount.size() == 1) return votedTargetsCount.keySet().iterator().next(); // First item

        List<Map.Entry<T, Integer>> highestVoteCounts = getTwoHighestVotes(votedTargetsCount);

        Map.Entry<T, Integer> highestVote = highestVoteCounts.get(0);
        Map.Entry<T, Integer> secondHighestVote = highestVoteCounts.get(1);

        // If the two highest votes are the same, nobody gets elected.
        // ---
        // ChatonDouteux  : 5 votes    | highestVote
        // SuperMangeChat : 5 votes    | secondHighestVote
        // JeSuisInno     : 4 votes    --------------------
        if (highestVote.getValue().equals(secondHighestVote.getValue())) return null;

        // If it's not the same count then it's the highestVote gets elected, for good or for worse.
        // ---
        // LGCramé        : 8 votes    | highestVote
        // EncoreUnLG     : 5 votes    | secondHighestVote
        return highestVote.getKey();
    }

    @Override
    public Map<T, Integer> getTargetVoteCount() {
        Map<T, Integer> votedTargetsCount = new HashMap<>();

        // Fill the votes count map
        getPicks().forEach((from, to) -> {
            int count = votedTargetsCount.getOrDefault(to, 0) + 1;
            votedTargetsCount.put(to, count);
        });
        return votedTargetsCount;
    }

    @Override
    public int getTotalVoteCount() {
        return getTargetVoteCount().values().stream().reduce(0, Integer::sum);
    }

    @Override
    public void safePick(@NotNull LGPlayer picker, @NotNull T target) {
        super.safePick(picker, target);
        Events.call(new LGPickEvent(orchestrator, createPick(picker, target)));
    }

    @Override
    protected @Nullable T safeRemovePick(LGPlayer picker, boolean isInvalidate) {
        T removedTarget = super.safeRemovePick(picker, isInvalidate);

        if (removedTarget != null) {
            Events.call(new LGPickRemovedEvent(orchestrator, createPick(picker, removedTarget), isInvalidate));
        }

        return removedTarget;
    }

    private Pick<T, E> createPick(@NotNull LGPlayer picker, @NotNull T target) {
        return new Pick<>(getEntry(), picker, target);
    }

    @NotNull
    private List<Map.Entry<T, Integer>> getTwoHighestVotes(Map<T, Integer> votedTargetsCount) {
        return votedTargetsCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(2)
                .collect(Collectors.toList());
    }
}
