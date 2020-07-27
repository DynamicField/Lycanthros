package com.github.jeuxjeux20.loupsgarous.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.interaction.*;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeContext;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeDeterminer;
import com.google.common.collect.ImmutableMultiset;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractVote<T>
        extends AbstractStatefulPick<T>
        implements Vote<T> {
    private final VoteOutcomeDeterminer<T> voteOutcomeDeterminer;

    public AbstractVote(LGGameOrchestrator orchestrator, Dependencies<T> dependencies) {
        super(orchestrator);
        this.voteOutcomeDeterminer = dependencies.voteOutcomeDeterminer;
    }

    @Override
    public VoteOutcome<T> getOutcome() {
        return voteOutcomeDeterminer.determine(createContext());
    }

    private VoteOutcomeContext<T> createContext() {
        return new VoteOutcomeContext<>(getVotes(), getPicks(), getClass(),
                orchestrator.game());
    }

    @Override
    public ImmutableMultiset<T> getVotes() {
        return ImmutableMultiset.copyOf(getPicks().values());
    }

    @Override
    public final boolean conclude() {
        throwIfClosed();
        closeAndReportException();

        VoteOutcome<T> outcome = getOutcome();
        return conclude(outcome);
    }

    protected abstract boolean conclude(VoteOutcome<T> outcome);

    @Override
    protected final void safePick(LGPlayer picker, T target) {
        super.safePick(picker, target);

        Events.call(new LGPickEvent(orchestrator, createPick(picker, target)));
    }

    @Override
    protected final @Nullable T safeRemovePick(LGPlayer picker, boolean isInvalidate) {
        T removedTarget = super.safeRemovePick(picker, isInvalidate);

        if (removedTarget != null) {
            Events.call(new LGPickRemovedEvent(orchestrator,
                    createPick(picker, removedTarget), isInvalidate));
        }

        return removedTarget;
    }

    private PickData<T, ?> createPick(LGPlayer picker, T target) {
        return new PickData<>(this, picker, target);
    }

    protected static class Dependencies<T> {
        public final VoteOutcomeDeterminer<T> voteOutcomeDeterminer;

        @Inject
        Dependencies(VoteOutcomeDeterminer<T> voteOutcomeDeterminer) {
            this.voteOutcomeDeterminer = voteOutcomeDeterminer;
        }
    }
}
