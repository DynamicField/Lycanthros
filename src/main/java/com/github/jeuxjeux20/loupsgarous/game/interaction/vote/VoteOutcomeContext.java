package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

public final class VoteOutcomeContext<T> {
    private final ImmutableMultiset<T> votes;
    private final ImmutableMap<LGPlayer, T> picks;
    private final Vote<T> vote;
    private final LGGameOrchestrator orchestrator;

    public VoteOutcomeContext(Multiset<T> votes, ImmutableMap<LGPlayer, T> picks,
                              Vote<T> vote, LGGameOrchestrator orchestrator) {
        this.votes = ImmutableMultiset.copyOf(votes);
        this.picks = picks;
        this.vote = vote;
        this.orchestrator = orchestrator;
    }

    public ImmutableMultiset<T> getVotes() {
        return votes;
    }

    public ImmutableMap<LGPlayer, T> getPicks() {
        return picks;
    }

    public Vote<T> getVote() {
        return vote;
    }

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }
}
