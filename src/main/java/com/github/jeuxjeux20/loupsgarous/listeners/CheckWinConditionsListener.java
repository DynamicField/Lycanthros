package com.github.jeuxjeux20.loupsgarous.listeners;

import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseStartingEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhaseDescriptor;
import com.github.jeuxjeux20.loupsgarous.winconditions.WinCondition;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.WIN_CONDITIONS;

public class CheckWinConditionsListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLGPhaseStarting(LGPhaseStartingEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();
        LGPhase phase = event.getPhase();
        LGPhaseDescriptor descriptor = orchestrator.phases().descriptors().get(phase.getClass());

        if (!orchestrator.isGameRunning() || descriptor.postponesWinConditions()) {
            return;
        }

        for (WinCondition winCondition : orchestrator.getGameBox().contents(WIN_CONDITIONS)) {
            Optional<LGEnding> ending = winCondition.check(orchestrator);
            if (ending.isPresent()) {
                orchestrator.finish(ending.get());
                return;
            }
        }
    }
}
