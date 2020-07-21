package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.SnapshotComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyCompositionChangeEvent;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;

public class MinecraftLGLobbyCompositionManager implements LGLobbyCompositionManager {
    private final LGGameOrchestrator orchestrator;
    private final CompositionValidator compositionValidator;

    private final LobbyComposition composition;
    private @Nullable CompositionValidator.Problem.Type worseCompositionProblemType;

    @Inject
    MinecraftLGLobbyCompositionManager(@Assisted LGGameOrchestrator orchestrator,
                                       @Assisted LGGameBootstrapData bootstrapData,
                                       CompositionValidator compositionValidator) {
        this.orchestrator = orchestrator;
        this.compositionValidator = compositionValidator;

        this.composition = new LobbyComposition(bootstrapData.getComposition());

        updateCompositionProblemType();
    }

    @Override
    public Composition get() {
        return new SnapshotComposition(composition);
    }

    @Override
    public Optional<MutableComposition> getMutable() {
        if (orchestrator.lobby().isLocked()) {
            return Optional.empty();
        }
        else {
            return Optional.of(composition);
        }
    }

    @Override
    public @Nullable CompositionValidator.Problem.Type getWorstProblemType() {
        return worseCompositionProblemType;
    }


    private void updateCompositionProblemType() {
        worseCompositionProblemType = compositionValidator.validate(composition).stream()
                .map(CompositionValidator.Problem::getType)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    public boolean isValid() {
        return getWorstProblemType() != CompositionValidator.Problem.Type.IMPOSSIBLE;
    }


    private final class LobbyComposition extends MutableComposition {
        public LobbyComposition(Composition composition) {
            super(composition);
        }

        @Override
        public boolean isValidPlayerCount(int playerCount) {
            return super.isValidPlayerCount(playerCount) &&
                   orchestrator.game().getPlayers().size() <= playerCount;
        }

        @Override
        protected void onChange() {
            updateCompositionProblemType();
            Events.call(new LGLobbyCompositionChangeEvent(orchestrator));
        }
    }
}
