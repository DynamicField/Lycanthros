package com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

import java.util.ArrayList;
import java.util.List;

class IntrinsicVoteOutcomeDeterminer<T> implements VoteOutcomeDeterminer<T> {
    @Override
    public VoteOutcome<T> determine(VoteOutcomeContext<T> context) {
        if (context.getPicks().size() == 0) {
            return new NoVotesVoteOutcome<>();
        }

        List<Multiset.Entry<T>> sameVotesCandidates = getHighestSameVotesCandidates(context.getVotes());

        if (sameVotesCandidates.size() == 1) {
            return new RelativeMajorityVoteOutcome<>(sameVotesCandidates.get(0).getElement());
        } else {
            ImmutableList<T> conflictingCandidates = sameVotesCandidates.stream()
                    .map(Multiset.Entry::getElement)
                    .collect(ImmutableList.toImmutableList());

            return new IndecisiveVoteOutcome<>(conflictingCandidates);
        }
    }

    private List<Multiset.Entry<T>> getHighestSameVotesCandidates(Multiset<T> votes) {
        List<Multiset.Entry<T>> highestVoteHolders = new ArrayList<>();
        int highestCountSeen = -1;

        for (Multiset.Entry<T> entry : votes.entrySet()) {
            int count = entry.getCount();

            if (highestCountSeen < count) {
                highestVoteHolders.clear();
                highestCountSeen = count;
            }
            if (highestCountSeen == count) {
                highestVoteHolders.add(entry);
            }
        }

        return highestVoteHolders;
    }
}
