package com.github.jeuxjeux20.loupsgarous.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.ExtensionPoint;
import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.AbstractStatefulPick;
import com.github.jeuxjeux20.loupsgarous.interaction.PickData;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeContext;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeDeterminer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeTransformer;
import com.google.common.collect.ImmutableMultiset;
import me.lucko.helper.Events;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractVote<T>
        extends AbstractStatefulPick<T>
        implements Vote<T> {
    private VoteOutcomeDeterminer<? super T> voteOutcomeDeterminer = VoteOutcomeDeterminer.DEFAULT;
    private final Class<T> candidateClass;

    public AbstractVote(LGGameOrchestrator orchestrator, Class<T> candidateClass) {
        super(orchestrator);
        this.candidateClass = candidateClass;
    }

    @Override
    public final VoteOutcome<T> getOutcome() {
        VoteOutcomeContext<T> context = createContext();
        VoteOutcome<T> outcome = voteOutcomeDeterminer.determine(context);
        ExtensionPoint<VoteOutcomeTransformer<T>> extensionPoint =
                LGExtensionPoints.voteOutcomeTransformers(candidateClass);

        for (VoteOutcomeTransformer<T> transformer : extensionPoint.getContents(orchestrator)) {
            outcome = transformer.transform(context, outcome);
        }

        return outcome;
    }

    private VoteOutcomeContext<T> createContext() {
        return new VoteOutcomeContext<>(getVotes(), getPicks(), getClass(), orchestrator);
    }

    protected VoteOutcomeDeterminer<? super T> getVoteOutcomeDeterminer() {
        return voteOutcomeDeterminer;
    }

    protected void setVoteOutcomeDeterminer(VoteOutcomeDeterminer<? super T> voteOutcomeDeterminer) {
        this.voteOutcomeDeterminer = voteOutcomeDeterminer;
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
}
