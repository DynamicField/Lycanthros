package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator;
import org.jetbrains.annotations.Nullable;

public interface LGLobbyCompositionManager {
    void openOwnerGui();

    Composition get();

    @Nullable CompositionValidator.Problem.Type getWorstProblemType();

    boolean isValid();

    interface Factory {
        LGLobbyCompositionManager create(LGGameOrchestrator orchestrator, LGGameBootstrapData bootstrapData);
    }
}
