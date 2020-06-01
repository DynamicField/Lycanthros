package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.events.lobby.LGLobbyCompositionChangeEvent;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GameStartStage extends AsyncLGGameStage implements CountdownTimedStage {
    static {
        Events.subscribe(LGLobbyCompositionChangeEvent.class)
                .handler(e -> {
                    LGGameStage currentStage = e.getOrchestrator().stages().current();
                    if (currentStage instanceof GameStartStage) {
                        GameStartStage stage = (GameStartStage) currentStage;
                        stage.countdown.setTimer(stage.countdown.getBiggestTimerValue());
                    }
                });
    }

    private final Countdown countdown;

    @Inject
    GameStartStage(@Assisted LGGameOrchestrator orchestrator) {
        super(orchestrator);

        this.countdown = new GameStartCountdown();
    }

    @Override
    public CompletableFuture<Void> run() {
        return countdown.start();
    }

    @Override
    public @Nullable String getName() {
        return null;
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    private class GameStartCountdown extends Countdown {
        public GameStartCountdown() {
            super(orchestrator.getPlugin(), 15);
        }

        @Override
        protected void onTick() {
            if (orchestrator.getState() != LGGameState.READY_TO_START) {
                setTimer(getBiggestTimerValue());
            } else if (getTimer() != 0 && getTimer() < 5) {
                orchestrator.getAllMinecraftPlayers().forEach(this::displayCountdown);
            }
        }

        private void displayCountdown(Player player) {
            player.sendTitle(ChatColor.YELLOW + String.valueOf(getTimer()), null, 3, 15, 3);
            LGSoundStuff.pling(player);
        }
    }
}
