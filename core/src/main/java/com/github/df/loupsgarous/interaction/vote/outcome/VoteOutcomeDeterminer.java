package com.github.df.loupsgarous.interaction.vote.outcome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

import java.util.ArrayList;
import java.util.List;

public interface VoteOutcomeDeterminer<T> {
    VoteOutcomeDeterminer<Object> DEFAULT = new Default();

    <C extends T> VoteOutcome<C> determine(VoteOutcomeContext<C> context);

    class Default implements VoteOutcomeDeterminer<Object> {
        protected Default() {}

        @Override
        public <C> VoteOutcome<C> determine(VoteOutcomeContext<C> context) {
            if (context.getPicks().size() == 0) {
                return new NoVotesVoteOutcome<>();
            }

            List<Multiset.Entry<C>> sameVotesCandidates = getHighestSameVotesCandidates(context.getVotes());

            if (sameVotesCandidates.size() == 1) {
                return new RelativeMajorityVoteOutcome<>(sameVotesCandidates.get(0).getElement());
            } else {
                ImmutableList<C> conflictingCandidates = sameVotesCandidates.stream()
                        .map(Multiset.Entry::getElement)
                        .collect(ImmutableList.toImmutableList());

                return new IndecisiveVoteOutcome<>(conflictingCandidates);
            }
        }

        private <C> List<Multiset.Entry<C>> getHighestSameVotesCandidates(Multiset<C> votes) {
            List<Multiset.Entry<C>> highestVoteHolders = new ArrayList<>();
            int highestCountSeen = -1;

            for (Multiset.Entry<C> entry : votes.entrySet()) {
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
}
