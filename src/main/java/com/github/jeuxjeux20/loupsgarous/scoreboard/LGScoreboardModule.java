package com.github.jeuxjeux20.loupsgarous.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;

public final class LGScoreboardModule extends ScoreboardComponentsModule {
    @Override
    protected void configureBindings() {
        bind(LGScoreboardManager.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.SCOREBOARD,
                        LGScoreboardManager.class);
            }
        });
    }

    @Override
    protected void configureScoreboardComponents() {
        addScoreboardComponent(LobbyOwnerScoreboardComponent.class);
        addScoreboardComponent(PlayersAliveScoreboardComponent.class);
        addScoreboardComponent(CompositionScoreboardComponent.class);
        addScoreboardComponent(CurrentVotesScoreboardComponent.class);
    }
}
