package com.github.jeuxjeux20.loupsgarous.game.interaction.finders;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;

import java.util.Set;

public class CurrentStageInteractableFinder implements InteractableFinder {
    @Override
    public Set<InteractableEntry<?>> find(LGGameOrchestrator orchestrator) {
        LGStage currentStage = orchestrator.stages().current();

        return InteractableFinder.fromPossibleProvider(currentStage).find(orchestrator);
    }
}
