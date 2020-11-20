package com.github.jeuxjeux20.loupsgarous.listeners;

import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseStartingEvent;
import com.github.jeuxjeux20.loupsgarous.game.FinishGameTransition;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.phases.Phase;
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
        Phase phase = event.getPhase();

        if (!orchestrator.isGameRunning() || phase.getDescriptor().postponesWinConditions()) {
            return;
        }

        for (WinCondition winCondition : WIN_CONDITIONS.getContents(orchestrator)) {
            Optional<LGEnding> ending = winCondition.check(orchestrator);
            if (ending.isPresent()) {
                orchestrator.stateTransitions().requestExecution(
                        new FinishGameTransition(ending.get())
                );
                return;
            }
        }
    }
}
