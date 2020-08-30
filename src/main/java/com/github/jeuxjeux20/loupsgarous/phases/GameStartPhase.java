package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyCompositionUpdateEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.google.inject.Inject;
import me.lucko.helper.Events;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class GameStartPhase extends CountdownLGPhase {
    @Inject
    GameStartPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        registerEventListeners();
    }

    @Override
    protected Countdown createCountdown() {
        return new GameStartCountdown();
    }

    private void registerEventListeners() {
        Events.subscribe(LGLobbyCompositionUpdateEvent.class)
                .handler(e -> getCountdown().setTimer(getCountdown().getBiggestTimerValue()))
                .bindWith(this);
    }

    private class GameStartCountdown extends Countdown {
        public GameStartCountdown() {
            super(15);
        }

        @Override
        protected void onTick() {
            if (orchestrator.getState() != LGGameState.READY_TO_START) {
                setTimer(getBiggestTimerValue());
            } else if (getTimer() != 0 && getTimer() <= 5) {
                orchestrator.getAllMinecraftPlayers().forEach(this::displayCountdown);
            }
        }

        private void displayCountdown(Player player) {
            player.sendTitle(ChatColor.YELLOW + String.valueOf(getTimer()), null, 3, 15, 3);
            LGSoundStuff.pling(player);
        }
    }
}
