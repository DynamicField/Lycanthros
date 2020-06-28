package com.github.jeuxjeux20.loupsgarous.game.interaction;

import java.util.Map;

public interface Votable<T> extends StatefulPickable<T>, NotifyingInteractable {
    String getPointingText();

    /**
     * Gets the target with the vote majority.
     *
     * @return the most voted target, or {@code null} if there isn't a clear distinction
     */
    T getMajorityTarget();

    /**
     * Gets a map with the value representing how much votes the target (the key) got.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * PykeDeMort    -> ElFamosoLG       | ElFamosoLG : 2 votes
     * AnnieDeMort   -> ElFamosoLG       | Woufe      : 1 vote
     * InsererPseudo -> Woufe            | LoL        : 1 vote
     * CETAIT_SUR    -> LoL              - }</pre>
     *
     * @return a map representing the targets and the count of votes they got
     */
    // TODO: Use a Multiset
    Map<T, Integer> getTargetVoteCount();

    int getTotalVoteCount();
}
