package com.github.jeuxjeux20.loupsgarous.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.google.inject.AbstractModule;

public final class LGScoreboardModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGScoreboardManager.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.SCOREBOARD,
                        LGScoreboardManager.class);
            }
        });
    }
}
