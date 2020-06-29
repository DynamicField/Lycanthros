package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.google.common.collect.Multiset;

public interface Votable<T> extends StatefulPickable<T>, Interactable {
    String getPointingText();

    /**
     * Gets the target with the vote majority.
     *
     * @return the most voted target, or {@code null} if there isn't a clear distinction
     */
    T getMajority();

    /**
     * Gets a multiset representing the occurrences of each voted players.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * PykeDeMort    -> ElFamosoLG       | ElFamosoLG : 2 votes
     * AnnieDeMort   -> ElFamosoLG       | Woufe      : 1 vote
     * InsererPseudo -> Woufe            | LoL        : 1 vote
     * CETAIT_SUR    -> LoL              - }</pre>
     *
     * @return a multiset representing the occurrences of each voted players
     */
    Multiset<T> getVotes();
}
