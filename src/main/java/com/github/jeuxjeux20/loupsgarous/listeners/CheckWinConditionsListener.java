package com.github.jeuxjeux20.loupsgarous.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseStartingEvent;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhaseDescriptor;
import com.github.jeuxjeux20.loupsgarous.winconditions.WinCondition;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.Set;

public class CheckWinConditionsListener implements Listener {
    private final Set<WinCondition> winConditions;

    @Inject
    CheckWinConditionsListener(Set<WinCondition> winConditions) {
        this.winConditions = winConditions;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLGPhaseStarting(LGPhaseStartingEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();
        LGPhase phase = event.getPhase();
        LGPhaseDescriptor descriptor = orchestrator.phases().descriptors().get(phase.getClass());

        if (!orchestrator.isGameRunning() || descriptor.postponesWinConditions()) {
            return;
        }

        for (WinCondition winCondition : winConditions) {
            Optional<LGEnding> ending = winCondition.check(orchestrator);
            if (ending.isPresent()) {
                orchestrator.finish(ending.get());
                return;
            }
        }
    }
}
