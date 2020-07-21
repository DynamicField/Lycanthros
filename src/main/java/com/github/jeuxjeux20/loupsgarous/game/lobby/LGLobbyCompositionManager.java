package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface LGLobbyCompositionManager {

    Composition get();

    Optional<MutableComposition> getMutable();

    @Nullable CompositionValidator.Problem.Type getWorstProblemType();

    boolean isValid();

    interface Factory {
        LGLobbyCompositionManager create(LGGameOrchestrator orchestrator, LGGameBootstrapData bootstrapData);
    }
}
