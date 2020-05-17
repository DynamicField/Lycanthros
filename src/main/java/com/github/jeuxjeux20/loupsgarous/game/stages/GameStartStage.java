package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GameStartStage extends AsyncLGGameStage {
    @Inject
    public GameStartStage(@Assisted LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public CompletableFuture<Void> run() {
        return new Countdown(orchestrator.getPlugin(), 5) {
            @Override
            protected void onTick() {
                String message = ChatColor.BLUE + "La partie va commencer dans " +
                                 ChatColor.YELLOW + getTimer() +
                                 ChatColor.BLUE + " secondes.";
                orchestrator.sendToEveryone(message);
            }
        }.start();
    }

    @Override
    public @Nullable String getName() {
        return null;
    }
}
