package com.github.jeuxjeux20.loupsgarous.game.interaction.finders;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableProvider;

import java.util.Collections;
import java.util.Set;

/**
 * Finds {@link InteractableEntry} objects from a game orchestrator.
 * <p>
 * When aggregating results of multiple finders, duplicate entries will be removed.
 */
@FunctionalInterface
public interface InteractableFinder {
    InteractableFinder EMPTY = o -> Collections.emptySet();

    Set<InteractableEntry<?>> find(LGGameOrchestrator orchestrator);

    static InteractableFinder fromPossibleProvider(Object possibleProvider) {
        if (possibleProvider instanceof InteractableProvider) {
            return fromProvider((InteractableProvider) possibleProvider);
        }
        else {
            return EMPTY;
        }
    }

    static InteractableFinder fromProvider(InteractableProvider interactableProvider) {
        return new InteractableProviderInteractableFinder(interactableProvider);
    }
}
