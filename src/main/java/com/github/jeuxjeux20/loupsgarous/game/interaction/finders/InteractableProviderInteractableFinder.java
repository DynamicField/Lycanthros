package com.github.jeuxjeux20.loupsgarous.game.interaction.finders;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableProvider;

import java.util.Set;

public class InteractableProviderInteractableFinder implements InteractableFinder {
    private final InteractableProvider interactableProvider;

    public InteractableProviderInteractableFinder(InteractableProvider interactableProvider) {
        this.interactableProvider = interactableProvider;
    }

    @Override
    public Set<InteractableEntry<?>> find(LGGameOrchestrator orchestrator) {
        return interactableProvider.getInteractables();
    }
}
