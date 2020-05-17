package com.github.df.loupsgarous.interaction.vote.outcome;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.vote.Vote;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import java.util.Map;

@SuppressWarnings("rawtypes") // Class<? extends RawType>
public final class VoteOutcomeContext<T> {
    private final ImmutableMultiset<T> votes;
    private final ImmutableMap<LGPlayer, T> picks;
    private final Class<? extends Vote> voteClass;
    private final LGGameOrchestrator orchestrator;

    public VoteOutcomeContext(Multiset<T> votes, Map<LGPlayer, T> picks,
                              Class<? extends Vote> voteClass, LGGameOrchestrator orchestrator) {
        this.votes = ImmutableMultiset.copyOf(votes);
        this.picks = ImmutableMap.copyOf(picks);
        this.voteClass = voteClass;
        this.orchestrator = orchestrator;
    }

    public ImmutableMultiset<T> getVotes() {
        return votes;
    }

    public ImmutableMap<LGPlayer, T> getPicks() {
        return picks;
    }

    public Class<? extends Vote> getVoteClass() {
        return voteClass;
    }

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }
}
