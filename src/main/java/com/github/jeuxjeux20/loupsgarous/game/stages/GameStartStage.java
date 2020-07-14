package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyCompositionChangeEvent;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameStartStage extends CountdownLGStage {
    @Inject
    GameStartStage(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected Countdown createCountdown() {
        return new GameStartCountdown();
    }

    static class ResetTimerListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onLGLobbyCompositionChange(LGLobbyCompositionChangeEvent event) {
            LGStage currentStage = event.getOrchestrator().stages().current();
            if (currentStage instanceof GameStartStage) {
                GameStartStage stage = (GameStartStage) currentStage;
                stage.getCountdown().setTimer(stage.getCountdown().getBiggestTimerValue());
            }
        }
    }

    private class GameStartCountdown extends Countdown {
        public GameStartCountdown() {
            super(15);
        }

        @Override
        protected void onTick() {
            if (orchestrator.state() != LGGameState.READY_TO_START) {
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
