package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGCompositionUpdateEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.StartGameTransition;
import me.lucko.helper.Events;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public final class LobbyPhase extends CountdownPhase {
    private static final int START_DELAY = 15;

    private @Nullable CompositionValidator.Problem.Type worstCompositionProblemType;

    public LobbyPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        registerEventListeners();
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.builder()
                .tick(this::tick)
                .build();
    }

    private void registerEventListeners() {
        Events.subscribe(LGCompositionUpdateEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> updateStatus(true))
                .bindWith(this);

        Events.merge(LGEvent.class, LGPlayerJoinEvent.class, LGPlayerQuitEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> updateStatus(false))
                .bindWith(this);
    }

    @Override
    protected void start() {
        updateStatus(false);
    }

    @Override
    protected void finish() {
        orchestrator.stateTransitions().requestExecution(new StartGameTransition());
    }

    @Override
    public boolean stop() {
        if (isStarting()) {
            return super.stop();
        } else {
            return false;
        }
    }

    private void tick() {
        if (getCountdown().getTimer() != 0 && getCountdown().getTimer() <= 5) {
            orchestrator.getAllMinecraftPlayers().forEach(this::displayCountdown);
        }
    }

    public @Nullable CompositionValidator.Problem.Type getWorstCompositionProblemType() {
        return worstCompositionProblemType;
    }

    public boolean isStarting() {
        return !getCountdown().isPaused();
    }

    private boolean canStart() {
       return orchestrator.isFull() &&
              worstCompositionProblemType != CompositionValidator.Problem.Type.IMPOSSIBLE;
    }

    private void updateStatus(boolean resetCountdown) {
        validateComposition();

        if (canStart()) {
            if (resetCountdown) {
                getCountdown().setTimer(START_DELAY);
            }
            getCountdown().setPaused(false);
        } else {
            getCountdown().setTimer(START_DELAY);
            getCountdown().setPaused(true);
        }
    }

    private void validateComposition() {
        CompositionValidator validator = CompositionValidator.getHandler(orchestrator);

        worstCompositionProblemType =
                validator.validate(orchestrator.getComposition()).stream()
                        .map(CompositionValidator.Problem::getType)
                        .max(Comparator.naturalOrder())
                        .orElse(null);
    }

    private void displayCountdown(Player player) {
        player.sendTitle(ChatColor.YELLOW + String.valueOf(getCountdown().getTimer()), null, 3, 15, 3);
        LGSoundStuff.pling(player);
    }
}
