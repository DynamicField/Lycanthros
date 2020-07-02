package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.game.interaction.StatefulPickable;
import com.google.common.collect.Multiset;

public interface Votable<T> extends StatefulPickable<T> {
    /**
     * Gets the text used in the middle of a vote message.
     * The vote message uses this template:
     * <blockquote><pre>{voter} {pointingText} {candidate}</pre></blockquote>
     * <p>
     * For example, a value of "vote pour" will send the following message when <i>Personne1</i> votes:
     * <blockquote><b>Personne1</b> vote pour <b>Personne2</b></blockquote>
     *
     * @return the text used in the middle of a vote message
     */
    String getPointingText();

    /**
     * Gets the outcome of the vote, which is determined by the relative majority.
     *
     * @return the vote outcome
     */
    VoteOutcome<T> getOutcome();

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
    Multiset<T> getVotes();
}
