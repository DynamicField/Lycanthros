package com.github.jeuxjeux20.loupsgarous.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.cards.composition.ImmutableComposition;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator;
import org.jetbrains.annotations.Nullable;

public interface LGLobbyCompositionManager {
    ImmutableComposition get();

    void update(Composition composition);

    @Nullable CompositionValidator.Problem.Type getWorstProblemType();

    boolean isValid();

    interface Factory {
        LGLobbyCompositionManager create(LGGameOrchestrator orchestrator, LGGameBootstrapData bootstrapData);
    }
}
