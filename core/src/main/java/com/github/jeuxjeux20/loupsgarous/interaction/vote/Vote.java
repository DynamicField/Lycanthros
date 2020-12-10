package com.github.jeuxjeux20.loupsgarous.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickAddedEvent;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.registry.GameRegistries;
import com.github.jeuxjeux20.loupsgarous.extensibility.registry.Registry;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.PickData;
import com.github.jeuxjeux20.loupsgarous.interaction.StatefulPick;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeContext;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeDeterminer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcomeTransformer;
import com.google.common.collect.ImmutableMultiset;
import me.lucko.helper.Events;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

public abstract class Vote<T>
        extends StatefulPick<T> {
    private VoteOutcomeDeterminer<? super T> voteOutcomeDeterminer = VoteOutcomeDeterminer.DEFAULT;
    private final Class<T> candidateClass;

    public Vote(LGGameOrchestrator orchestrator, Class<T> candidateClass) {
        super(orchestrator);
        this.candidateClass = candidateClass;
    }

    /**
     * Gets the outcome of the vote, which is determined by the relative majority.
     *
     * @return the vote outcome
     */
    public final VoteOutcome<T> getOutcome() {
        VoteOutcomeContext<T> context = createContext();
        VoteOutcome<T> outcome = voteOutcomeDeterminer.determine(context);

        Registry<VoteOutcomeTransformer<T>> registry =
                GameRegistries.voteOutcomeTransformers(candidateClass).get(orchestrator);

        for (VoteOutcomeTransformer<T> transformer : registry) {
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

    /**
     * Gets a multiset representing the occurrences of each vote.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * PykeDeMort    -> ElFamosoLG       | ElFamosoLG : 2 occurrences
     * AnnieDeMort   -> ElFamosoLG       | Woufe      : 1 occurrences
     * InsererPseudo -> Woufe            | LoL        : 1 occurrences
     * CETAIT_SUR    -> LoL              - }</pre>
     *
     * @return a multiset representing the occurrences of each vote
     */
    public ImmutableMultiset<T> getVotes() {
        return ImmutableMultiset.copyOf(getPicks().values());
    }

    /**
     * Closes this vote, and execute actions based on the outcome.
     *
     * @return {@code true} if the conclusion led to a significant action, {@code false} if not
     * @throws IllegalStateException when the vote is already closed
     */
    public final boolean conclude() {
        VoteOutcome<T> outcome = getOutcome();
        try {
            return conclude(outcome);
        } finally {
            unregister();
        }
    }

    protected abstract boolean conclude(VoteOutcome<T> outcome);

    @Override
    protected final void safePick(LGPlayer picker, T target) {
        super.safePick(picker, target);

        if (isRegistered()) {
            Events.call(new LGPickAddedEvent(orchestrator, createPick(picker, target)));
        }
    }

    @Override
    protected final @Nullable T safeRemovePick(LGPlayer picker, boolean isInvalidate) {
        T removedTarget = super.safeRemovePick(picker, isInvalidate);

        if (removedTarget != null && isRegistered()) {
            Events.call(new LGPickRemovedEvent(orchestrator,
                    createPick(picker, removedTarget), isInvalidate));
        }

        return removedTarget;
    }

    private PickData<T> createPick(LGPlayer picker, T target) {
        return new PickData<>(this, picker, target);
    }

    /**
     * Gets the text used in the middle of a vote message. The vote message uses this template:
     * <blockquote><pre>{voter} {pointingText} {candidate}</pre></blockquote>
     * <p>
     * For example, a value of "vote pour" will send the following message when <i>Personne1</i>
     * votes:
     * <blockquote><b>Personne1</b> vote pour <b>Personne2</b></blockquote>
     *
     * @return the text used in the middle of a vote message
     */
    public abstract String getPointingText();

    public abstract ChatColor getHighlightColor();

    public void togglePick(LGPlayer picker, T target) {
        if (getPicks().get(picker) == target) {
            removePick(picker);
        } else {
            pick(picker, target);
        }
    }
}
