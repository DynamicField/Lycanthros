package com.github.jeuxjeux20.loupsgarous.game.interaction.finders;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.google.inject.Inject;

import java.util.Set;
import java.util.stream.Collectors;

class InteractableFinderAggregator implements InteractableFinder {
    private final Set<InteractableFinder> interactableFinders;

    @Inject
    InteractableFinderAggregator(Set<InteractableFinder> interactableFinders) {
        this.interactableFinders = interactableFinders;
    }

    @Override
    public Set<InteractableEntry<?>> find(LGGameOrchestrator orchestrator) {
        return interactableFinders.stream().flatMap(x -> x.find(orchestrator).stream()).collect(Collectors.toSet());
    }
}
